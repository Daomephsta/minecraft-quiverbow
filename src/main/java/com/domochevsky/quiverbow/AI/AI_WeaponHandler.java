package com.domochevsky.quiverbow.AI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.ammo.ColdIronClip;
import com.domochevsky.quiverbow.ammo.GatlingAmmo;
import com.domochevsky.quiverbow.ammo.GoldMagazine;
import com.domochevsky.quiverbow.ammo.LapisMagazine;
import com.domochevsky.quiverbow.ammo.LargeNetherrackMagazine;
import com.domochevsky.quiverbow.ammo.LargeRedstoneMagazine;
import com.domochevsky.quiverbow.ammo.LargeRocket;
import com.domochevsky.quiverbow.ammo.NeedleMagazine;
import com.domochevsky.quiverbow.ammo.ObsidianMagazine;
import com.domochevsky.quiverbow.ammo.RedstoneMagazine;
import com.domochevsky.quiverbow.ammo.RocketBundle;
import com.domochevsky.quiverbow.ammo.SeedJar;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.weapons.*;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;

public class AI_WeaponHandler
{
    public static void setFirstWeapon(Entity_AA turret, ItemStack weapon) // Sets
									  // our
									  // weapon
									  // directly
									  // via
									  // ID,
									  // no
									  // questions
									  // asked
    {
	AI_Storage.dropFirstWeapon(turret); // Begone with what I'm currently
					    // holding

	turret.firstWeapon = (_WeaponBase) weapon.getItem();

	turret.setHeldItem(EnumHand.MAIN_HAND, weapon); // Equip
	AI_Targeting.setAttackRange(turret);

	turret.hasFirstWeapon = true;
	turret.hasToldOwnerAboutFirstAmmo = false; // Reset
    }

    public static void setSecondWeapon(Entity_AA turret, ItemStack weapon) // Sets
									   // our
									   // weapon
									   // directly
									   // via
									   // ID,
									   // no
									   // questions
									   // asked
    {
	AI_Storage.dropSecondWeapon(turret); // Begone with what I'm currently
					     // holding

	turret.secondWeapon = (_WeaponBase) weapon.getItem();

	turret.setHeldItem(EnumHand.OFF_HAND, weapon); // Equip

	turret.hasSecondWeapon = true;
	turret.hasToldOwnerAboutSecondAmmo = false; // Reset
    }

    public static void attackTarget(Entity_AA turret, boolean secondRail)
    {
	attackTarget(turret, secondRail, false);
    }

    public static void attackTarget(Entity_AA turret, boolean secondRail, boolean ignoreValidation)
    {
	if (!ignoreValidation)
	{
	    if (secondRail)
	    {
		if (!AI_Targeting.canAttackTarget(turret, turret.secondWeapon))
		{
		    return;
		}
	    }
	    else
	    {
		if (!AI_Targeting.canAttackTarget(turret, turret.firstWeapon))
		{
		    return;
		}
	    } // Not happening
	}
	// else, not doing target validation

	_WeaponBase currentWeapon = null;
	ItemStack currentStack = ItemStack.EMPTY;

	if (secondRail)
	{
	    if (turret.firstWeapon != null && !turret.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) // Checking
											      // fire
											      // staggering
	    {
		if (AI_Targeting.isNameOnWhitelist(turret, Commands.cmdStaggerFire)) // Instructed
										     // to
										     // fire
										     // our
										     // weapons
										     // alternating
		{
		    if (turret.firstWeapon.getCooldown(
			    turret.getHeldItem(EnumHand.MAIN_HAND)) != (turret.firstWeapon.getMaxCooldown() / 2))
		    {
			return; // Not ready yet. Only firing when the primary
				// weapon cooldown has reached half of its max
		    }
		}
	    }

	    currentWeapon = turret.secondWeapon;
	    currentStack = turret.getHeldItem(EnumHand.OFF_HAND);
	}
	else
	{
	    currentWeapon = turret.firstWeapon;
	    currentStack = turret.getHeldItem(EnumHand.MAIN_HAND);
	}

	// Still need to check we're actually holding anything
	if (ignoreValidation && (currentWeapon == null || currentStack.isEmpty()))
	{
	    return;
	} // Nothing to attack with? Weird

	int currentDmg = currentWeapon.getDamage(currentStack);

	// We're out of ammo
	if (currentDmg >= currentStack.getMaxDamage())
	{
	    turret.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.3F);

	    if (secondRail)
	    {
		turret.secondAttackDelay = 40;
	    }
	    else
	    {
		turret.firstAttackDelay = 40;
	    }

	    reloadFromStorage(turret, secondRail); // Try to reload if you can

	    return;
	}

	// Special case
	if (currentWeapon instanceof Crossbow_Auto)
	{
	    if (currentStack.hasTagCompound() && !currentStack.getTagCompound().getBoolean("isChambered"))
	    {
		// Is the auto crossbow and not chambered. Doing that now
		currentStack.getTagCompound().setBoolean("isChambered", true); // Done,
									       // we're
									       // good
									       // to
									       // go
									       // again

		// SFX
		turret.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 0.5F);

		// This long until you can fire again
		if (secondRail)
		{
		    turret.secondAttackDelay = currentWeapon.getMaxCooldown() + 1;
		}
		else
		{
		    turret.firstAttackDelay = currentWeapon.getMaxCooldown() + 1;
		}

		return; // We're done here
	    }
	}

	if (secondRail)
	{
	    turret.posY -= 0.4;
	} // Pos adjustment for the second rail

	if (!ignoreValidation)
	{
	    AI_Targeting.lookAtTarget(turret, secondRail);
	} // Just making sure

	currentWeapon.doSingleFire(currentStack, turret.world, turret); // BLAM

	if (secondRail)
	{
	    turret.posY += 0.4;
	} // Pos adjustment for the second rail

	// consumeAmmo(turret, secondRail); // Should now be properly handled by
	// the weapons themselves

	// This long until you can fire again
	if (secondRail)
	{
	    turret.secondAttackDelay = currentWeapon.getMaxCooldown() + 1;
	}
	else
	{
	    turret.firstAttackDelay = currentWeapon.getMaxCooldown() + 1;
	}
    }

    // Known to be on server side
    private static void reloadFromStorage(Entity_AA turret, boolean secondRail)
    {
	_WeaponBase currentWeapon = null;
	ItemStack currentStack = ItemStack.EMPTY;

	if (secondRail)
	{
	    currentWeapon = turret.secondWeapon;
	    currentStack = turret.getHeldItem(EnumHand.OFF_HAND);
	}
	else
	{
	    currentWeapon = turret.firstWeapon;
	    currentStack = turret.getHeldItem(EnumHand.MAIN_HAND);
	}

	if (turret.ownerName != null && turret.ownerName.equals("Herobrine")) // We're
									      // naturally
									      // hostile,
									      // so
									      // reloading
									      // is
									      // infinite
	{
	    currentStack.setItemDamage(0); // Fill
	    return;
	}

	int slot = 0;
	boolean sendMsg = false;

	while (slot < turret.storage.length) // Lesse what I got here...
	{
	    if (!turret.storage[slot].isEmpty())
	    {
		if (currentWeapon instanceof LapisCoil && turret.storage[slot].getItem() instanceof LapisMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof OSP && turret.storage[slot].getItem() instanceof ObsidianMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof OSR && turret.storage[slot].getItem() instanceof ObsidianMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof OWR && turret.storage[slot].getItem() instanceof ObsidianMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof NetherBellows
			&& turret.storage[slot].getItem() instanceof LargeNetherrackMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof RedSprayer
			&& turret.storage[slot].getItem() instanceof LargeRedstoneMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof LightningRed
			&& turret.storage[slot].getItem() instanceof RedstoneMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof CoinTosser && turret.storage[slot].getItem() instanceof GoldMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof CoinTosser_Mod
			&& turret.storage[slot].getItem() instanceof GoldMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof SugarEngine && turret.storage[slot].getItem() instanceof GatlingAmmo)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof ThornSpitter
			&& turret.storage[slot].getItem() instanceof NeedleMagazine)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof SeedSweeper && turret.storage[slot].getItem() instanceof SeedJar)
		{
		    currentStack.setItemDamage(turret.storage[slot].getItemDamage()); // Fill
		    turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage()); // Empty
		    sendMsg = true;
		}

		else if (currentWeapon instanceof RPG_Imp && turret.storage[slot].getItem() instanceof LargeRocket)
		{
		    currentStack.setItemDamage(0); // Fill
		    decreaseStackSize(turret, slot, 1);
		}

		else if (currentWeapon instanceof DragonBox && turret.storage[slot].getItem() instanceof RocketBundle)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 8)); // 8
															 // shots
															 // per
															 // bundle
		}

		else if (currentWeapon instanceof DragonBox_Quad
			&& turret.storage[slot].getItem() instanceof RocketBundle)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 2)); // 2
															 // shots
															 // per
															 // bundle
		}

		else if (currentWeapon instanceof Crossbow_Compact && turret.storage[slot].getItem() == Items.ARROW)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 1));
		}

		else if (currentWeapon instanceof Crossbow_Double && turret.storage[slot].getItem() == Items.ARROW)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 2));
		}

		else if (currentWeapon instanceof Crossbow_Blaze && turret.storage[slot].getItem() == Items.BLAZE_ROD)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 1));
		}

		else if (currentWeapon instanceof Crossbow_Auto
			&& turret.storage[slot].getItem() instanceof ArrowBundle)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 8)); // 1
															 // bundle
															 // for
															 // 8
															 // shots
		}

		else if (currentWeapon instanceof Crossbow_AutoImp
			&& turret.storage[slot].getItem() instanceof ArrowBundle)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 2) * 8)); // 2
															 // bundles
															 // for
															 // 16
															 // shots
		}

		else if (currentWeapon instanceof FrostLancer && turret.storage[slot].getItem() instanceof ColdIronClip)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 4)); // 1
															 // ammo
															 // for
															 // 4
															 // shots
		}

		else if (currentWeapon instanceof EnderRifle && turret.storage[slot].getItem() == Items.IRON_INGOT)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 8));
		}

		else if (currentWeapon instanceof SilkenSpinner
			&& turret.storage[slot].getItem() == Item.getItemFromBlock(Blocks.WEB))
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 1));
		}

		else if (currentWeapon instanceof SnowCannon
			&& turret.storage[slot].getItem() == Item.getItemFromBlock(Blocks.SNOW))
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 16));
		}

		else if (currentWeapon instanceof Mortar_Arrow && turret.storage[slot].getItem() instanceof ArrowBundle)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 8)); // 1
														   // shot
														   // per
														   // bundle,
														   // can
														   // hold
														   // 8
														   // bundles
		}

		else if (currentWeapon instanceof Mortar_Dragon
			&& turret.storage[slot].getItem() instanceof RocketBundle)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 8)); // 1
														   // shot
														   // per
														   // bundle,
														   // can
														   // hold
														   // 8
														   // bundles
		}

		else if (currentWeapon instanceof Potatosser && turret.storage[slot].getItem() == Items.POTATO)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 14));
		}

		else if (currentWeapon instanceof SoulCairn && turret.storage[slot].getItem() == Items.DIAMOND)
		{
		    currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 14));
		}

		// ---

		if (sendMsg) // something changed, so telling all clients about
			     // it
		{
		    NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret,
			    Item.getIdFromItem(turret.storage[slot].getItem()), slot,
			    turret.storage[slot].getItemDamage());
		}

		if (currentStack.getItemDamage() < currentStack.getMaxDamage())
		{
		    // Reset
		    if (secondRail)
		    {
			turret.hasToldOwnerAboutSecondAmmo = false;
		    }
		    else
		    {
			turret.hasToldOwnerAboutFirstAmmo = false;
		    }

		    return; // We've loaded something in, so we're done here
		}
	    }
	    // else there's nothing in that slot

	    slot += 1;
	}

	// Done going through storage and didn't find anything to reload with.
	if (turret.hasCommunicationUpgrade)
	{
	    if (AI_Targeting.isNameOnWhitelist(turret, Commands.cmdTellAmmo)) // Informing
									      // the
									      // owner
									      // when
									      // they're
									      // out
									      // of
									      // ammo
	    {
		if (secondRail && !turret.hasToldOwnerAboutSecondAmmo)
		{
		    AI_Communication.tellOwnerAboutAmmo(turret, secondRail);
		    turret.hasToldOwnerAboutSecondAmmo = true;
		}
		else if (!secondRail && !turret.hasToldOwnerAboutFirstAmmo)
		{
		    AI_Communication.tellOwnerAboutAmmo(turret, secondRail);
		    turret.hasToldOwnerAboutFirstAmmo = true;
		}
	    }
	}
	// else, no com upgrade. Nevermind
    }

    // Returns the amount removed (in case the leftovers are less than what was
    // asked for)
    private static int decreaseStackSize(Entity_AA turret, int slot, int amount)
    {
	if (slot < 0 || slot > turret.storage.length)
	{
	    return 0;
	} // Out of bounds
	if (turret.storage[slot].isEmpty())
	{
	    return 0;
	} // Nothing in there

	int amountRemoved = amount;

	if (turret.storage[slot].getCount() < amount) // Less left than we need
	{
	    amountRemoved = turret.storage[slot].getCount(); // Adjusting how
							     // much we remove

	    turret.storage[slot] = ItemStack.EMPTY;

	    // Something changed, so informing the client about that now
	    NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret, -1, slot, 0);

	    // return amountRemoved;
	}
	else // Has as much as we need
	{
	    turret.storage[slot].shrink(amount);

	    // Still some left?
	    if (turret.storage[slot].getCount() <= 0) // Nope
	    {
		turret.storage[slot] = ItemStack.EMPTY; // Remove it

		// Something changed, so informing the client about that now
		NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret, -1, slot, 0);
	    }
	}

	return amountRemoved;
    }

    public static void fireWithOwner(Entity_AA turret, boolean secondRail)
    {
	if (!(turret.getControllingPassenger() instanceof EntityPlayer))
	{
	    return;
	} // Not a living thing riding us, so can't hold weapons

	EntityPlayer rider = (EntityPlayer) turret.getControllingPassenger();

	ItemStack targeter = InventoryHelper.findItemInHandsByClass(rider, AA_Targeter.class);
	if (targeter.isEmpty())
	{
	    return;
	} // Isn't holding the targeter

	AA_Targeter weapon = (AA_Targeter) targeter.getItem();

	if (weapon.getCooldown(targeter) <= 0)
	{
	    return;
	} // Isn't firing

	turret.rotationPitch = rider.rotationPitch;

	// Checks out. The owner's weapon is on cooldown, meaning they've fired
	// very recently. Getting in on that.
	// We should already be looking where the owner is looking
	attackTarget(turret, secondRail, true);
    }
}
