package com.domochevsky.quiverbow.ai;

import com.domochevsky.quiverbow.ammo.*;
import com.domochevsky.quiverbow.armsassistant.EntityAA;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.util.InventoryHelper;
import com.domochevsky.quiverbow.weapons.*;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class AIWeaponHandler
{
	public static void setFirstWeapon(EntityAA turret, ItemStack weapon) // Sets
	// our
	// weapon
	// directly
	// via
	// ID,
	// no
	// questions
	// asked
	{
		AIStorage.dropFirstWeapon(turret); // Begone with what I'm currently
		// holding

		turret.firstWeapon = (WeaponBase) weapon.getItem();

		turret.setHeldItem(EnumHand.MAIN_HAND, weapon); // Equip
		AITargeting.setAttackRange(turret);

		turret.hasFirstWeapon = true;
		turret.hasToldOwnerAboutFirstAmmo = false; // Reset
	}

	public static void setSecondWeapon(EntityAA turret, ItemStack weapon) // Sets
	// our
	// weapon
	// directly
	// via
	// ID,
	// no
	// questions
	// asked
	{
		AIStorage.dropSecondWeapon(turret); // Begone with what I'm currently
		// holding

		turret.secondWeapon = (WeaponBase) weapon.getItem();

		turret.setHeldItem(EnumHand.OFF_HAND, weapon); // Equip

		turret.hasSecondWeapon = true;
		turret.hasToldOwnerAboutSecondAmmo = false; // Reset
	}

	public static void attackTarget(EntityAA turret, boolean secondRail)
	{
		attackTarget(turret, secondRail, false);
	}

	public static void attackTarget(EntityAA turret, boolean secondRail, boolean ignoreValidation)
	{
		if (!ignoreValidation)
		{
			if (secondRail)
			{
				if (!AITargeting.canAttackTarget(turret, turret.secondWeapon))
				{
					return;
				}
			}
			else
			{
				if (!AITargeting.canAttackTarget(turret, turret.firstWeapon))
				{
					return;
				}
			} // Not happening
		}
		// else, not doing target validation

		WeaponBase currentWeapon = null;
		ItemStack currentStack = ItemStack.EMPTY;

		if (secondRail)
		{
			if (turret.firstWeapon != null && !turret.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) // Checking
			// fire
			// staggering
			{
				if (AITargeting.isNameOnWhitelist(turret, Commands.cmdStaggerFire)) // Instructed
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
		if (currentWeapon instanceof CrossbowAuto)
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
			AITargeting.lookAtTarget(turret, secondRail);
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
	private static void reloadFromStorage(EntityAA turret, boolean secondRail)
	{
		WeaponBase currentWeapon = null;
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

				else if (currentWeapon instanceof CoinTosserMod
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

				else if (currentWeapon instanceof RPGImp && turret.storage[slot].getItem() == ItemRegistry.LARGE_ROCKET)
				{
					currentStack.setItemDamage(0); // Fill
					decreaseStackSize(turret, slot, 1);
				}

				else if (currentWeapon instanceof DragonBox && turret.storage[slot].getItem() == ItemRegistry.ROCKET_BUNDLE)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 8)); // 8
					// shots
					// per
					// bundle
				}

				else if (currentWeapon instanceof DragonBoxQuad
						&& turret.storage[slot].getItem() == ItemRegistry.ROCKET_BUNDLE)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 2)); // 2
					// shots
					// per
					// bundle
				}

				else if (currentWeapon instanceof CrossbowCompact && turret.storage[slot].getItem() == Items.ARROW)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 1));
				}

				else if (currentWeapon instanceof CrossbowDouble && turret.storage[slot].getItem() == Items.ARROW)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 2));
				}

				else if (currentWeapon instanceof CrossbowBlaze && turret.storage[slot].getItem() == Items.BLAZE_ROD)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 1));
				}

				else if (currentWeapon instanceof CrossbowAuto
						&& turret.storage[slot].getItem() == ItemRegistry.ARROW_BUNDLE)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 8)); // 1
					// bundle
					// for
					// 8
					// shots
				}

				else if (currentWeapon instanceof CrossbowAutoImp
						&& turret.storage[slot].getItem() == ItemRegistry.ARROW_BUNDLE)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 2) * 8)); // 2
					// bundles
					// for
					// 16
					// shots
				}

				else if (currentWeapon instanceof FrostLancer && turret.storage[slot].getItem() == ItemRegistry.COLD_IRON_CLIP)
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

				else if (currentWeapon instanceof MortarArrow && turret.storage[slot].getItem() == ItemRegistry.ARROW_BUNDLE)
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

				else if (currentWeapon instanceof MortarDragon
						&& turret.storage[slot].getItem() == ItemRegistry.ARROW_BUNDLE)
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
					NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret, turret.storage[slot], slot);
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
			if (AITargeting.isNameOnWhitelist(turret, Commands.cmdTellAmmo)) // Informing
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
					AICommunication.tellOwnerAboutAmmo(turret, secondRail);
					turret.hasToldOwnerAboutSecondAmmo = true;
				}
				else if (!secondRail && !turret.hasToldOwnerAboutFirstAmmo)
				{
					AICommunication.tellOwnerAboutAmmo(turret, secondRail);
					turret.hasToldOwnerAboutFirstAmmo = true;
				}
			}
		}
		// else, no com upgrade. Nevermind
	}

	// Returns the amount removed (in case the leftovers are less than what was
	// asked for)
	private static int decreaseStackSize(EntityAA turret, int slot, int amount)
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
			NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret, ItemStack.EMPTY, slot);
		}
		else // Has as much as we need
		{
			turret.storage[slot].shrink(amount);

			// Still some left?
			if (turret.storage[slot].getCount() <= 0) // Nope
			{
				turret.storage[slot] = ItemStack.EMPTY; // Remove it
				// Something changed, so informing the client about that now
				NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret, ItemStack.EMPTY, slot);
			}
		}

		return amountRemoved;
	}

	public static void fireWithOwner(EntityAA turret, boolean secondRail)
	{
		if (!(turret.getControllingPassenger() instanceof EntityPlayer))
		{
			return;
		} // Not a living thing riding us, so can't hold weapons

		EntityPlayer rider = (EntityPlayer) turret.getControllingPassenger();

		ItemStack targeter = InventoryHelper.findItemInHandsByClass(rider, AATargeter.class);
		if (targeter.isEmpty())
		{
			return;
		} // Isn't holding the targeter

		AATargeter weapon = (AATargeter) targeter.getItem();

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
