package com.domochevsky.quiverbow.weapons.base;

import java.util.List;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.ammosource.AmmoSource;
import com.domochevsky.quiverbow.weapons.base.trigger.Trigger;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

public final class Weapon extends Item
{
    private final WeaponProperties properties;
    private final Trigger trigger;
    private Effect[] fireEffects;
    private Effect[] cooldownEffects;
    private EnumAction useAction = EnumAction.NONE;
    private int maxUseTicks = 0;

    public Weapon(String name, WeaponProperties.Builder propertiesBuilder, Trigger trigger)
    {
        propertiesBuilder.setId(new ResourceLocation(QuiverbowMain.MODID, name));
        this.properties = propertiesBuilder.build();
        this.trigger = trigger;
        setRegistryName(QuiverbowMain.MODID, name);
        setUnlocalizedName(QuiverbowMain.MODID + ".weapon." + name);
        setCreativeTab(QuiverbowMain.QUIVERBOW_TAB);
        setMaxStackSize(1);
        trigger.adjustItemProperties(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        ActionResult<ItemStack> triggerResult = trigger.usePressed(world, player, stack, hand, properties);
        if (triggerResult.getType() == EnumActionResult.SUCCESS)
            applyEffects(fireEffects, world, player, stack);
        return triggerResult;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
        if (trigger.useTick(player.getEntityWorld(), stack, player, properties, count))
            applyEffects(fireEffects, player.getEntityWorld(), player, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft)
    {
        if(trigger.useReleased(world, entityLiving, stack, properties))
            applyEffects(fireEffects, world, entityLiving, stack);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase living, ItemStack stack)
    {
        // onEntitySwing is called again while swinging for some reason
        if (living.isSwingInProgress)
            return false;
        if (trigger.attackPressed(living.getEntityWorld(), living, stack, properties))
            applyEffects(fireEffects, living.getEntityWorld(), living, stack);
        return false;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
    {
        if (getCooldown(stack) > 0)
            setCooldown(stack, getCooldown(stack) - 1);
        if (getCooldown(stack) == 1 && entity instanceof EntityLivingBase) // Check at 1 to avoid infinite effects
            applyEffects(cooldownEffects, world, (EntityLivingBase) entity, stack);
        if (entity instanceof EntityLivingBase &&
            trigger.weaponTick(world, (EntityLivingBase) entity, stack, properties))
        {
            applyEffects(fireEffects, world, (EntityLivingBase) entity, stack);
        }
    }

    public static void setCooldown(ItemStack stack, int cooldown)
    {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("cooldown", cooldown); // Done
    }

    public static int getCooldown(ItemStack stack)
    {
        if (!stack.hasTagCompound())
            return 0;
        return stack.getTagCompound().getInteger("cooldown");
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack)
    {
        // QB:R weapons are not differentiated by damage or NBT
        return oldStack.isItemEqualIgnoreDurability(newStack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        // QB:R weapons are not differentiated by damage or NBT
        return !oldStack.isItemEqualIgnoreDurability(newStack);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
    {
        AmmoSource ammoSource = getTrigger().getAmmoSource();
        list.add(I18n.format(getUnlocalizedName() + ".ammostatus",
            ammoSource.getAmmo(stack), ammoSource.getAmmoCapacity(stack)));
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        if (!properties.isEnabled())
            return null;
        return super.getCreativeTab();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            AmmoSource ammoSource = getTrigger().getAmmoSource();
            ItemStack stack = new ItemStack(this);
            int capacity = ammoSource.getAmmoCapacity(stack);
            ammoSource.addAmmo(stack, capacity);
            items.add(stack);
        }
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        AmmoSource ammoSource = getTrigger().getAmmoSource();
        return (double) (ammoSource.getAmmoCapacity(stack) - ammoSource.getAmmo(stack)) /
            (double) ammoSource.getAmmoCapacity(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return getTrigger().getAmmoSource().getAmmoCapacity(stack) != -1;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return useAction;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return maxUseTicks;
    }

    public WeaponProperties getProperties()
    {
        return properties;
    }

    public Trigger getTrigger()
    {
        return trigger;
    }

    public Weapon fireEffects(Effect... fireEffects)
    {
        this.fireEffects = fireEffects;
        return this;
    }

    public Weapon cooldownEffects(Effect... cooldownEffects)
    {
        this.cooldownEffects = cooldownEffects;
        return this;
    }

    private void applyEffects(Effect[] effects, World world, EntityLivingBase entity, ItemStack stack)
    {
        if (effects != null)
        {
            for (Effect effect : effects)
                effect.apply(world, entity, stack, properties);
        }
    }

    public Weapon setUseParameters(EnumAction action, int maxUseTicks)
    {
        this.useAction = action;
        this.maxUseTicks = maxUseTicks;
        return this;
    }

    public static interface Effect
    {
        public void apply(World world, EntityLivingBase shooter, ItemStack stack, WeaponProperties properties);
    }
}
