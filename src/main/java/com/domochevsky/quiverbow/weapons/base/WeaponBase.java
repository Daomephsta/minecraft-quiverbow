package com.domochevsky.quiverbow.weapons.base;

import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.miscitems.QuiverBowItem;
import com.domochevsky.quiverbow.util.Newliner;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.FiringBehaviourBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.IFiringBehaviour;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class WeaponBase extends QuiverBowItem
{
	protected String name;
	public boolean enabled;

	public int damageMin;
	public int damageMax;

	public double speed;
	private double firingSpeed;

	public int knockback;
	public byte kickback;

	public int cooldown;

	protected boolean isMobUsable;

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

	public String getName()
	{
		return this.name;
	}

	public boolean isMobUsable()
	{
		return this.isMobUsable;
	} // Usable by default

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean unknown)
	{
		if (player.capabilities.isCreativeMode) list.add(I18n.format(Constants.MODID + ".ammo.infinite"));
		else
			Collections.addAll(list, Newliner.translateAndParse(getUnlocalizedName() + ".ammostatus",
					stack.getMaxDamage() - stack.getItemDamage(), stack.getMaxDamage()));
		Collections.addAll(list, Newliner.translateAndParse(getUnlocalizedName() + ".loadtext"));
		super.addInformation(stack, player, list, unknown);
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
		return this.cooldown;
	}

	public int getCooldown(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return 0;
		} // Why are you not holding anything?
		if (stack.getTagCompound() == null)
		{
			return 0;
		} // No tag, no cooldown

		return stack.getTagCompound().getInteger("cooldown"); // Here ya go
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
		if (this.getDamage(stack) >= stack.getMaxDamage())
		{
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		} // Is empty

		firingBehaviour.fire(stack, world, player, hand);

		return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
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

	// Regular fire, as called by onItemRightClick. To be overridden by each
	// individual weapon
	// Can also be called by mobs
	public void doSingleFire(ItemStack stack, World world, Entity entity)
	{}

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
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		subItems.add(new ItemStack(item, 1, 0));
		subItems.add(Helper.createEmptyWeaponOrAmmoStack(item, 1));
	}

	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{}

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

	public void setFiringSpeed(int speed)
	{
		if (speed <= 0)
		{
			this.firingSpeed = 0.1d;
		}
		else
		{
			this.firingSpeed = speed;
		}
	}

	public double getFiringSpeed()
	{
		return this.firingSpeed;
	}
}
