package com.domochevsky.quiverbow.armsassistant;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.armsassistant.ai.EntityAIFollowOwner;
import com.domochevsky.quiverbow.armsassistant.ai.EntityAIMaintainPosition;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Streams;
import com.google.gson.JsonParseException;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.Constants.NBT;

public class ArmsAssistantDirectives
{
    private static final Splitter ON_NEWLINE = Splitter.onPattern("\\n").omitEmptyStrings().trimResults(),
                                  ON_WHITEPSACE = Splitter.onPattern("\\s").omitEmptyStrings().trimResults();

    private EntityArmsAssistant armsAssistant;
    private final BiPredicate<EntityArmsAssistant, EntityLiving> targetSelector,
                                                                 targetBlacklist;
    private final MovementAI movementAI;
    private final Collection<EntityAIBase> aiTasks = new ArrayList<>();

    private ArmsAssistantDirectives(EntityArmsAssistant armsAssistant)
    {
        this.armsAssistant = armsAssistant;
        this.targetSelector = (directedEntity, target) -> IMob.MOB_SELECTOR.apply(target);
        this.targetBlacklist = (directedEntity, target) -> false;
        this.movementAI = MovementAI.NONE;
    }

    private ArmsAssistantDirectives(Builder builder)
    {
        this.armsAssistant = builder.armsAssistant;
        this.targetSelector = builder.targetSelectors.stream().reduce(BiPredicate::or).orElse((directedEntity, target) -> false);
        this.targetBlacklist = builder.targetBlacklist.stream().reduce(BiPredicate::or).orElse((directedEntity, target) -> false);
        this.movementAI = builder.movementAI;
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
        NBTTagList pages = book.getTagCompound().getTagList("pages", NBT.TAG_STRING);
        if (book.getItem() == Items.WRITABLE_BOOK)
        {
            List<String> lines = NBTPrimitiveStreams.toStringStream(pages)
                .flatMap(pageText -> ON_NEWLINE.splitToList(pageText).stream())
                .collect(Collectors.toList());
            return fromLines(armsAssistant, lines, errorHandler);
        }
        else if (book.getItem() == Items.WRITTEN_BOOK)
        {
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < pages.tagCount(); i++)
            {
                String page = pages.getStringTagAt(i);
                try
                {
                    String pageText = ITextComponent.Serializer.jsonToComponent(page).getUnformattedText();
                    lines.addAll(ON_NEWLINE.splitToList(pageText));
                }
                catch (JsonParseException e)
                {
                    e.printStackTrace();
                }
            }
            return fromLines(armsAssistant, lines, errorHandler);
        }
        throw new RuntimeException("Unreachable");
    }

    private static ArmsAssistantDirectives fromLines(EntityArmsAssistant armsAssistant, Iterable<String> lines, Consumer<ITextComponent> errorHandler)
    {
        Builder builder = new Builder(armsAssistant);
        for (String line : lines)
        {
            PeekingIterator<String> tokens = Iterators.peekingIterator(ON_WHITEPSACE.split(line).iterator());
            switch (tokens.next())
            {
                case "TARGET":
                    parseTargetingDirective(tokens, builder.targetSelectors::add, errorHandler);
                    break;
                case "IGNORE":
                    parseTargetingDirective(tokens, builder.targetBlacklist::add, errorHandler);
                    break;
                case "STAY":
                    builder.movementAI = MovementAI.STAY;
                    expectEOL(tokens, errorHandler);
                    break;
                case "FOLLOW":
                    builder.movementAI = MovementAI.FOLLOW_OWNER;
                    expectEOL(tokens, errorHandler);
                    break;
                default:
                    //TODO error
                    break;
            }
        }
        return new ArmsAssistantDirectives(builder);
    }

    private static void parseTargetingDirective(PeekingIterator<String> tokens, Consumer<BiPredicate<EntityArmsAssistant, EntityLiving>> out, Consumer<ITextComponent> errorHandler)
    {
        if (!tokens.hasNext())
        {
            errorHandler.accept(new TextComponentTranslation(
                QuiverbowMain.MODID + ".arms_assistant.directives.missingIdOrClass"));
            return;
        }
        BiPredicate<EntityArmsAssistant, EntityLiving> targetingDirective = null;
        while (tokens.hasNext())
        {
            BiPredicate<EntityArmsAssistant, EntityLiving> subPredicate = parseIdOrClass(tokens.next(), errorHandler);
            if (subPredicate != null)
                targetingDirective = targetingDirective.and(subPredicate);
        }
        if (targetingDirective != null)
            out.accept(targetingDirective);
    }

    private static BiPredicate<EntityArmsAssistant, EntityLiving> parseIdOrClass(String targetString, Consumer<ITextComponent> errorHandler)
    {
        if (targetString.equals("hostile"))
            return (directedEntity, target) -> IMob.MOB_SELECTOR.apply(target);
        else if (targetString.equals("friendly"))
            return (directedEntity, target) -> directedEntity.isOnSameTeam(target);
        else if (targetString.equals("injured"))
            return (directedEntity, target) -> target.getHealth() < target.getMaxHealth();
        else if (targetString.equals("flying"))
            return (directedEntity, target) -> target instanceof EntityFlying || target instanceof EntityBat;
        else
        {
            ResourceLocation id = new ResourceLocation(targetString);
            if (!EntityList.isRegistered(id))
            {
                errorHandler.accept(new TextComponentTranslation(
                    QuiverbowMain.MODID + ".arms_assistant.directives.unknownEntity", id));
                return null;
            }
            return (directedEntity, target) -> EntityList.isMatchingName(target, id);
        }
    }

    private static void expectEOL(Iterator<String> tokens, Consumer<ITextComponent> errorHandler)
    {
        if (tokens.hasNext())
        {
            errorHandler.accept(new TextComponentTranslation(
                QuiverbowMain.MODID + ".arms_assistant.directives.extraTokens", Streams.stream(tokens).collect(joining(" "))));
        }
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
        applyTask(armsAssistant.targetTasks, 2, new EntityAINearestAttackableTarget<>(armsAssistant,
            EntityLiving.class, 10, true, false, this::isValidTarget));
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
        return targetSelector.test(armsAssistant, candidate) && !targetBlacklist.test(armsAssistant, candidate);
    }

    public boolean areCustom()
    {
        return true;
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

        Builder(EntityArmsAssistant armsAssistant)
        {
            this.armsAssistant = armsAssistant;
        }
    }
}
