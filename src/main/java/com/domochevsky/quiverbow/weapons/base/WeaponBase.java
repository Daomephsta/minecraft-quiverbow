package com.domochevsky.quiverbow.weapons.base;

import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.miscitems.QuiverBowItem;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.FiringBehaviourBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.IFiringBehaviour;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

public abstract class WeaponBase extends QuiverBowItem
{
	protected String name;
	private WeaponProperties properties;

	protected IFiringBehaviour firingBehaviour = new FiringBehaviourBase<WeaponBase>(this)
	{
		@Override
		public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
		{
			System.out.println("No firing behavour implemented");
		}
	};

	public WeaponBase(String name, int maxAmmo)
	{
		this.setMaxStackSize(1); // Default is 64
		this.setMaxDamage(maxAmmo); // Default is 0
		this.setHasSubtypes(true); // Got a subtype, since we're using damage
		// values
		this.setFull3D(); // Not as thin as paper when held. Probably not
		// relevant when using models
		this.setCreativeTab(CreativeTabs.COMBAT);// On the combat tab by
		// default, since this is a
		// weapon
		this.name = name;
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.capabilities.isCreativeMode)
			list.add(I18n.format(QuiverbowMain.MODID + ".ammo.infinite"));
		else
			list.add(I18n.format(getUnlocalizedName() + ".ammostatus",
					stack.getMaxDamage() - stack.getItemDamage(), stack.getMaxDamage()));
		list.add(I18n.format(getUnlocalizedName() + ".loadtext"));
		super.addInformation(stack, world, list, flags);
	}

	// Removes the passed in value from the ammo stack
	// Returns true if the ammo has been used up
	public boolean consumeAmmo(ItemStack stack, Entity entity, int ammo)
	{
		// if (!(entity instanceof EntityPlayer)) { return false; } // Not a
		// player, so not deducting ammo. Keep going!

		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			if (player.capabilities.isCreativeMode)
			{
				return false;
			} // Is in creative mode, so not changing ammo for them either. Keep
				// going!
		}

		this.setDamage(stack, stack.getItemDamage() + ammo);

		if (stack.getItemDamage() >= stack.getMaxDamage()) // All used up. This
		// thing is now empty
		{
			this.setDamage(stack, stack.getMaxDamage()); // Just making sure
			// we're not going over
			// the cap
			return true;
		}

		return false; // There's still some left
	}

	public String getName()
	{
		return this.name;
	}

	public boolean isMobUsable()
	{
		return this.getProperties().isMobUsable();
	}

	public int getKickback()
	{
		return this.getProperties().getKickback();
	}
	
	public float getProjectileSpeed()
	{
		return this.getProperties().getProjectileSpeed();
	}

	public void resetCooldown(ItemStack stack)
	{
		setCooldown(stack, ((WeaponBase) stack.getItem()).getMaxCooldown());
	}

	public void setCooldown(ItemStack stack, int cooldown)
	{
		if (stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		} // Init

		stack.getTagCompound().setInteger("cooldown", cooldown); // Done
	}

	public int getMaxCooldown()
	{
		return getProperties().getMaxCooldown();
	}

	public int getCooldown(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return 0;
		}
		if (stack.getTagCompound() == null)
		{
			return 0;
		}
		return stack.getTagCompound().getInteger("cooldown");
	}

	public void setBurstFire(ItemStack stack, int amount) // Setting our burst
															// fire to
	// this amount. Assumes the
	// tag to be valid
	{
		if (stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		} // Init
		stack.getTagCompound().setInteger("burstFireLeft", amount);
	}

	public int getBurstFire(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return 0;
		} // Not a valid item
		if (!stack.hasTagCompound())
		{
			return 0;
		} // Doesn't have a tag

		return stack.getTagCompound().getInteger("burstFireLeft");
	}

	public void setFiringBehaviour(IFiringBehaviour firingBehaviour)
	{
		this.firingBehaviour = firingBehaviour;
	}

	public IFiringBehaviour getFiringBehaviour()
	{
		return firingBehaviour;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		return doSingleFire(world, player, stack, hand)
			? ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack)
			: ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	}

	public boolean doSingleFire(World world, EntityLivingBase entity, ItemStack stack, EnumHand hand)
	{
		if (this.getDamage(stack) >= stack.getMaxDamage())
			return false;
		firingBehaviour.fire(stack, world, entity, hand);
		return true;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
	{
		if (this.getCooldown(stack) > 0)
		{
			this.setCooldown(stack, this.getCooldown(stack) - 1);
		} // Cooling down
		if (this.getCooldown(stack) == 1)
		{
			this.doCooldownSFX(world, entity);
		} // One tick before cooldown is done with, so SFX now
		firingBehaviour.update(stack, world, entity, animTick, holdingItem);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		firingBehaviour.onStopFiring(stack, worldIn, entityLiving, timeLeft);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
	{
		firingBehaviour.onFiringTick(stack, player, count);
	}

	// Called one tick before cooldown is dealt with
	protected void doCooldownSFX(World world, Entity entity)
	{}

	public void doFireFX(World world, Entity entity)
	{}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	} // Always showing this bar, since it acts as ammo display

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1.0d / stack.getMaxDamage() * this.getDamage(stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if (!isInCreativeTab(tab)) return;
		subItems.add(new ItemStack(this, 1, 0));
		subItems.add(Helper.createEmptyWeaponOrAmmoStack(this, 1));
	}
	
	protected abstract WeaponProperties createDefaultProperties();

	public WeaponProperties getProperties()
	{
		if(properties == null) properties = createDefaultProperties();
		return properties;
	}

	protected String displayInSec(int tick)
	{
		return String.format("%.2f", tick * 0.05);
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book)
	{
		return false;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 40;
	}
}
