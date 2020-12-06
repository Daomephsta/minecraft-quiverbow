package com.domochevsky.quiverbow.armsassistant;

import static com.domochevsky.quiverbow.util.brigadier.ListArgumentType.getList;
import static com.domochevsky.quiverbow.util.brigadier.ListArgumentType.list;
import static com.domochevsky.quiverbow.util.brigadier.ResourceLocationArgumentType.getResourceLocation;
import static com.domochevsky.quiverbow.util.brigadier.ResourceLocationArgumentType.resourceLocation;
import static com.google.common.collect.Streams.stream;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.armsassistant.ai.ArmsAssistantAITargeterControlled;
import com.domochevsky.quiverbow.armsassistant.ai.EntityAIFollowOwner;
import com.domochevsky.quiverbow.armsassistant.ai.EntityAIMaintainPosition;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.google.common.base.Splitter;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import daomephsta.umbra.streams.NBTPrimitiveStreams;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants.NBT;

public class ArmsAssistantDirectives
{
    private static final String LANG_PREFIX = QuiverbowMain.MODID + ".arms_assistant";
    private static final Splitter ON_NEWLINE = Splitter.onPattern("\\n").trimResults();
    private static final CommandDispatcher<Builder> PARSER = buildParser();

    private EntityArmsAssistant armsAssistant;
    private final BiPredicate<EntityArmsAssistant, EntityLiving> targetSelector,
                                                                 targetBlacklist;
    private final MovementAI movementAI;
    boolean notifyLowHealth, notifyDeath;
    int notifyLowAmmoThreshold = -1;
    private final boolean remoteFire, staggerFire, safetyRange;
    private final Collection<EntityAIBase> aiTasks = new ArrayList<>();

    private ArmsAssistantDirectives(EntityArmsAssistant armsAssistant)
    {
        this.armsAssistant = armsAssistant;
        this.targetSelector = (directedEntity, target) -> IMob.MOB_SELECTOR.apply(target);
        this.targetBlacklist = (directedEntity, target) -> false;
        this.movementAI = MovementAI.NONE;
        this.notifyLowHealth = this.notifyDeath = false;
        this.notifyLowAmmoThreshold = -1;
        this.remoteFire = this.staggerFire = this.safetyRange = false;
    }

    private ArmsAssistantDirectives(Builder builder)
    {
        this.armsAssistant = builder.armsAssistant;
        this.targetSelector = builder.targetSelectors.stream().reduce(BiPredicate::or).orElse((directedEntity, target) -> false);
        this.targetBlacklist = builder.targetBlacklist.stream().reduce(BiPredicate::or).orElse((directedEntity, target) -> false);
        this.movementAI = builder.movementAI;
        this.notifyLowHealth = builder.notifyLowHealth;
        this.notifyDeath = builder.notifyDeath;
        this.notifyLowAmmoThreshold = builder.notifyLowAmmoThreshold;
        this.remoteFire = builder.remoteFire;
        this.staggerFire = builder.staggerFire;
        this.safetyRange = builder.safetyRange;
    }

    public static ArmsAssistantDirectives defaultDirectives(EntityArmsAssistant armsAssistant)
    {
        return new ArmsAssistantDirectives(armsAssistant)
        {
            @Override
            public boolean areCustom()
            {
                return false;
            }
        };
    }

    public static ArmsAssistantDirectives from(EntityArmsAssistant armsAssistant, ItemStack book, Consumer<ITextComponent> errorHandler)
    {
        if (book.getItem() != Items.WRITTEN_BOOK && book.getItem() != Items.WRITABLE_BOOK)
            throw new IllegalArgumentException("Directives can only be parsed from Writable or Written Books");
        if (!book.hasTagCompound())
            return new ArmsAssistantDirectives(armsAssistant);
        NBTTagList pagesTag = book.getTagCompound().getTagList("pages", NBT.TAG_STRING);
        if (book.getItem() == Items.WRITABLE_BOOK)
        {
            List<List<String>> lines = NBTPrimitiveStreams.toStringStream(pagesTag)
                .map(pageText -> ON_NEWLINE.splitToList(pageText))
                .collect(toList());
            return fromLines(armsAssistant, lines, errorHandler);
        }
        else if (book.getItem() == Items.WRITTEN_BOOK)
        {
            List<List<String>> pages = new ArrayList<>(pagesTag.tagCount());
            for (int i = 0; i < pagesTag.tagCount(); i++)
            {
                String page = pagesTag.getStringTagAt(i);
                try
                {
                    String pageText = ITextComponent.Serializer.jsonToComponent(page).getUnformattedText();
                    pages.add(ON_NEWLINE.splitToList(pageText));
                }
                catch (JsonParseException e)
                {
                    e.printStackTrace();
                }
            }
            return fromLines(armsAssistant, pages, errorHandler);
        }
        throw new RuntimeException("Unreachable");
    }

    private static ArmsAssistantDirectives fromLines(EntityArmsAssistant armsAssistant,
        Iterable<? extends Iterable<String>> pages, Consumer<ITextComponent> errorHandler)
    {
        Builder builder = new Builder(armsAssistant);
        int pageNumber = 0;
        for (Iterable<String> lines : pages)
        {
            pageNumber += 1;
            int lineNumber = 0;
            for (String line : lines)
            {
                lineNumber += 1;
                if (line.isEmpty()) continue;
                try
                {
                    ParseResults<Builder> parse = PARSER.parse(line, builder);
                    PARSER.execute(parse);
                }
                catch (CommandSyntaxException e)
                {
                    errorHandler.accept(new TextComponentTranslation(
                        LANG_PREFIX + ".directives.errorPrefix", pageNumber, lineNumber)
                        .appendText(e.getMessage()));
                }
            }
        }
        return new ArmsAssistantDirectives(builder);
    }

    private static CommandDispatcher<Builder> buildParser()
    {
        CommandDispatcher<Builder> dispatcher = new CommandDispatcher<>();
        dispatcher.register(targetingDirective("TARGET", (builder, selector) -> builder.targetSelectors.add(selector)));
        dispatcher.register(targetingDirective("IGNORE", (builder, selector) -> builder.targetBlacklist.add(selector)));
        dispatcher.register(literal("STAY").executes(ctx ->
        {
            ctx.getSource().movementAI = MovementAI.STAY;
            return Command.SINGLE_SUCCESS;
        }));
        dispatcher.register(literal("FOLLOW").executes(ctx ->
        {
            ctx.getSource().movementAI = MovementAI.FOLLOW_OWNER;
            return Command.SINGLE_SUCCESS;
        }));
        dispatcher.register(tellDirective());
        dispatcher.register(literal("REMOTE").then(literal("FIRE")
            .executes(ctx ->
            {
                ctx.getSource().remoteFire = true;
                return Command.SINGLE_SUCCESS;
            }))
        );
        dispatcher.register(literal("STAGGER").then(literal("FIRE")
            .executes(ctx ->
            {
                ctx.getSource().staggerFire = true;
                return Command.SINGLE_SUCCESS;
            }))
        );
        dispatcher.register(literal("SAFETY").then(literal("RANGE")
            .executes(ctx ->
            {
                ctx.getSource().safetyRange = true;
                return Command.SINGLE_SUCCESS;
            }))
        );
        return dispatcher;
    }

    private static final DynamicCommandExceptionType UNKNOWN_ENTITY
        = new DynamicCommandExceptionType(entityId -> new LiteralMessage("Unknown entity ID '" + entityId + "'"));
    private static LiteralArgumentBuilder<Builder> targetingDirective(String rootName, BiConsumer<Builder, BiPredicate<EntityArmsAssistant, EntityLiving>> out)
    {
        return literal(rootName)
            .then
            (
                argument("entity_id", resourceLocation()).then
                (
                    argument("conditions", list(string())).executes(ctx ->
                    {
                        List<String> conditions = getList(ctx, "conditions");
                        ResourceLocation entityId = getResourceLocation(ctx, "entity_id");
                        if (!EntityList.isRegistered(entityId))
                            throw UNKNOWN_ENTITY.create(entityId);

                        BiPredicate<EntityArmsAssistant, EntityLiving> targetingDirective = (directedEntity, target) -> EntityList.isMatchingName(target, entityId);
                        for (String condition : conditions)
                            targetingDirective = targetingDirective.and(parseEntityCondition(condition));
                        if (targetingDirective != null)
                            out.accept(ctx.getSource(), targetingDirective);
                        return Command.SINGLE_SUCCESS;
                    })
                ).executes(ctx ->
                {
                    ResourceLocation entityId = getResourceLocation(ctx, "entity_id");
                    if (!EntityList.isRegistered(entityId))
                        throw UNKNOWN_ENTITY.create(entityId);
                    out.accept(ctx.getSource(), (directedEntity, target) ->
                        EntityList.isMatchingName(target, entityId));
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then
            (
                argument("conditions", list(string())).executes(ctx ->
                {
                    List<String> conditions = getList(ctx, "conditions");
                    BiPredicate<EntityArmsAssistant, EntityLiving> targetingDirective = null;
                    for (String condition : conditions)
                    {
                        targetingDirective = targetingDirective != null
                            ? targetingDirective.and(parseEntityCondition(condition))
                            : parseEntityCondition(condition);
                    }
                    if (targetingDirective != null)
                        out.accept(ctx.getSource(), targetingDirective);
                    return Command.SINGLE_SUCCESS;
                })
            );
    }

    private static final DynamicCommandExceptionType UNKNOWN_ENTITY_CONDITION
        = new DynamicCommandExceptionType(condition -> new LiteralMessage("Unknown entity condition '" + condition + "'"));
    private static BiPredicate<EntityArmsAssistant, EntityLiving> parseEntityCondition(String condition) throws CommandSyntaxException
    {
        if (condition.equals("HOSTILE"))
            return (directedEntity, target) -> IMob.MOB_SELECTOR.apply(target);
        else if (condition.equals("FRIENDLY"))
            return (directedEntity, target) -> directedEntity.isOnSameTeam(target);
        else if (condition.equals("INJURED"))
            return (directedEntity, target) -> target.getHealth() < target.getMaxHealth();
        else if (condition.equals("FLYING"))
            return (directedEntity, target) -> target instanceof EntityFlying || target instanceof EntityBat;
        else
            throw UNKNOWN_ENTITY_CONDITION.create(condition);
    }

    private static LiteralArgumentBuilder<Builder> tellDirective()
    {
        return literal("TELL")
            .then
            (
                literal("HEALTH").then(literal("LOW").executes(ctx ->
                {
                    ctx.getSource().notifyLowHealth = true;
                    return Command.SINGLE_SUCCESS;
                }))
            )
            .then
            (
                literal("DEATH").executes(ctx ->
                {
                    ctx.getSource().notifyDeath = true;
                    return Command.SINGLE_SUCCESS;
                })
            )
            .then
            (
                literal("AMMO").then
                (
                    argument("threshold", integer(0)).executes(ctx ->
                    {
                        ctx.getSource().notifyLowAmmoThreshold = getInteger(ctx, "threshold");
                        return Command.SINGLE_SUCCESS;
                    })
                )
            );
    }


    private static LiteralArgumentBuilder<Builder> literal(String name)
    {
        return LiteralArgumentBuilder.literal(name);
    }

    private static <T> RequiredArgumentBuilder<Builder, T> argument(String name, ArgumentType<T> type)
    {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public void revertAI()
    {
        for (EntityAIBase task : aiTasks)
        {
            armsAssistant.tasks.removeTask(task);
            armsAssistant.targetTasks.removeTask(task);
        }
        aiTasks.clear();
    }

    public void applyAI()
    {
        if (remoteFire)
            applyTask(armsAssistant.tasks, 3, new ArmsAssistantAITargeterControlled(armsAssistant));
        applyTask(armsAssistant.targetTasks, 3, new EntityAINearestAttackableTarget<>(armsAssistant,
            EntityLiving.class, 0, true, false, this::isValidTarget));
        switch (movementAI)
        {
        case STAY:
            applyTask(armsAssistant.tasks, 1, new EntityAIMaintainPosition(armsAssistant, armsAssistant.getPosition(), 1, 0.5D));
            break;

        case FOLLOW_OWNER:
            applyTask(armsAssistant.tasks, 1, new EntityAIFollowOwner<>(armsAssistant, 8, 0.5D));
            break;

        default:
            break;
        }
    }

    private <T extends EntityAIBase> T applyTask(EntityAITasks tasks, int priority, T task)
    {
        tasks.addTask(priority, task);
        aiTasks.add(task);
        return task;
    }

    private boolean isValidTarget(EntityLiving candidate)
    {
        if (safetyRange)
        {
            float maxDamageRange = stream(armsAssistant.getHeldEquipment())
                .filter(stack -> stack.getItem() instanceof Weapon)
                .map(stack -> ((Weapon) stack.getItem()).getProperties())
                .filter(props -> props.has(CommonProperties.EXPLOSION_SIZE))
                .map(props -> props.getFloat(CommonProperties.EXPLOSION_SIZE))
                .max(Float::compareTo)
                .orElse(0.0F) + 2.0F; // Extra distance, to be safe
            if (armsAssistant.getDistance(candidate) <= maxDamageRange)
                return false;
        }
        return targetSelector.test(armsAssistant, candidate) && !targetBlacklist.test(armsAssistant, candidate);
    }

    public void onDamage(DamageSource source, float amount)
    {
        if (notifyLowHealth && armsAssistant.getOwner() != null
            && armsAssistant.getHealth() < armsAssistant.getMaxHealth() / 3.0F)
        {
            armsAssistant.getOwner().sendMessage(
                new TextComponentTranslation(LANG_PREFIX + ".messages.low_health", armsAssistant.getName()));
        }
    }

    public void onDeath(DamageSource source)
    {
        if (notifyDeath && armsAssistant.getOwner() != null)
        {
            armsAssistant.getOwner().sendMessage(
                new TextComponentTranslation(LANG_PREFIX + ".messages.death", armsAssistant.getName()));
        }
    }

    public void onReload(int reloadsRemaining)
    {
        if (notifyLowAmmoThreshold == reloadsRemaining && armsAssistant.getOwner() != null)
        {
            armsAssistant.getOwner().sendMessage(new TextComponentTranslation(
                LANG_PREFIX + ".messages.low_ammo", armsAssistant.getName(), reloadsRemaining));
        }
    }

    public boolean areCustom()
    {
        return true;
    }

    public boolean shouldStaggerFire()
    {
        return staggerFire;
    }

    private static enum MovementAI
    {
        STAY,
        FOLLOW_OWNER,
        NONE;
    }

    private static class Builder
    {
        private final EntityArmsAssistant armsAssistant;
        Collection<BiPredicate<EntityArmsAssistant, EntityLiving>> targetSelectors = new ArrayList<>(),
                                                                   targetBlacklist = new ArrayList<>();
        MovementAI movementAI = MovementAI.NONE;
        boolean notifyLowHealth = false,
                notifyDeath = false;
        int notifyLowAmmoThreshold = -1;
        boolean remoteFire = false,
                staggerFire = false,
                safetyRange = false;

        Builder(EntityArmsAssistant armsAssistant)
        {
            this.armsAssistant = armsAssistant;
        }
    }
}
