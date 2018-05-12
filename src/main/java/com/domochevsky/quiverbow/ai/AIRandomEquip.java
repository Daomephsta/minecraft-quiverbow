package com.domochevsky.quiverbow.ai;

import java.util.Random;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.armsassistant.EntityAA;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.item.ItemStack;

public class AIRandomEquip
{
	public static void setupGear(EntityAA turret)
	{
		if (turret.world.isRemote)
		{
			return;
		} // Not doing this on client side

		setUpgrades(turret); // Step 1, sorting out what they can do
		setWeapons(turret); // Step 2, what do they hold?
		setItems(turret); // Step 3, ammo
	}

	private static void setUpgrades(EntityAA turret)
	{
		// What do you have available?
		Random rand = new Random();

		AIProperties.applyMobilityUpgrade(turret); // Always

		int odds = rand.nextInt(10); // 0-9 vs 7+
		if (odds >= 7)
		{
			AIProperties.applyArmorUpgrade(turret);
		}

		odds = rand.nextInt(20); // 0-19 vs 19
		if (odds >= 19)
		{
			AIProperties.applyPlatingUpgrade(turret);
		}

		odds = rand.nextInt(20); // 0-19 vs 19
		if (odds >= 19)
		{
			AIProperties.applyWeaponUpgrade(turret);
		}
	}

	private static void setWeapons(EntityAA turret)
	{
		Random rand = new Random();
		int randNum = 0;

		int attempts = 0; // How often we tried
		WeaponBase weapon = null;

		while (weapon == null) // Until we hit a valid weapon
		{
			randNum = rand.nextInt(Main.weapons.size()); // Grabbing a number
			// between the list's
			// length and its
			// starting point

			weapon = Main.weapons.get(randNum);

			if (weapon != null && !weapon.isMobUsable())
			{
				weapon = null;
			} // Cannot be used by mobs, so begone
			else if (weapon != null && !weapon.enabled)
			{
				weapon = null;
			} // Is disabled, so begone

			attempts += 1;

			if (attempts >= 1000)
			{
				System.out.println(
						"[QUIVERBOW] Weapon randomizer: Couldn't find a valid first weapon for an Arms Assistant. Giving up.");
				return;
			}
		}

		AIWeaponHandler.setFirstWeapon(turret, new ItemStack(weapon)); // Should
		// have
		// a
		// weapon
		// now

		if (!turret.hasWeaponUpgrade)
		{
			return;
		} // Doesn't have a second rail, so we're done here

		attempts = 0; // Reset
		weapon = null;

		while (weapon == null) // Until we hit a valid weapon
		{
			randNum = rand.nextInt(Main.weapons.size()); // Grabbing a number
			// between the list's
			// length and its
			// starting point

			weapon = Main.weapons.get(randNum);

			if (weapon != null && !weapon.isMobUsable())
			{
				weapon = null;
			} // Cannot be used by mobs, so begone
			else if (weapon != null && !weapon.enabled)
			{
				weapon = null;
			} // Is disabled, so begone

			attempts += 1;

			if (attempts >= 1000)
			{
				System.out.println(
						"[QUIVERBOW] Weapon randomizer: Couldn't find a valid second weapon for an Arms Assistant. Giving up.");
				return;
			}
		}

		AIWeaponHandler.setSecondWeapon(turret, new ItemStack(weapon));
	}

	private static void setItems(EntityAA turret)
	{
		// Might not be necessary, since we're not dropping that stuff anyway
	}
}
