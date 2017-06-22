package com.domochevsky.quiverbow.AI;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.net.NetHelper;

public class AI_Properties
{
    // Set when this thing gets spawned and has the upgrade (crafted the packed
    // up AA with something to set that nbttag)
    public static void applyStorageUpgrade(Entity_AA turret)
    {
	turret.hasStorageUpgrade = true;

	ItemStack[] previousItems = turret.storage; // Lemme hold onto that for
						    // a moment...

	turret.storage = new ItemStack[8]; // Double capacity

	if (!turret.world.isRemote)
	{
	    return;
	} // Done on client side

	int counter = 0;

	// Not expecting this thing to have any existing items, since you can't
	// pack it up with items equipped
	// But doing it anyway, just in case
	while (counter < previousItems.length && counter < turret.storage.length)
	{
	    turret.storage[counter] = previousItems[counter]; // Attaching
							      // whatever was in
							      // there to the
							      // new inventory

	    counter += 1;
	}
    }

    // Letting all clients in range know what I look like
    public static void sendStateToPlayersInRange(Entity_AA turret)
    {
	NetHelper.sendTurretStateMessageToPlayersInRange(turret.world, turret, turret.hasArmorUpgrade,
		turret.hasWeaponUpgrade, turret.hasRidingUpgrade, turret.hasHeavyPlatingUpgrade,
		turret.hasCommunicationUpgrade);
    }

    // Telling them what I'm carrying in my pockets
    public static void sendInventoryToPlayersInRange(Entity_AA turret)
    {
	int counter = 0;

	while (counter < turret.storage.length)
	{
	    if (turret.storage[counter].isEmpty())
	    {
		NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret, -1, counter, 0); // Empty
	    }
	    else
	    {
		NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret,
			Item.getIdFromItem(turret.storage[counter].getItem()), counter,
			turret.storage[counter].getItemDamage());
	    }

	    counter += 1;
	}
    }

    // Gets applied by the (pre-crafted/upgraded) packed up AA
    public static void applyArmorUpgrade(Entity_AA turret)
    {
	turret.hasArmorUpgrade = true; // Adding one now

	turret.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40); // Doubling
											// the
											// max
											// health
    }

    public static void applyPlatingUpgrade(Entity_AA turret)
    {
	turret.hasHeavyPlatingUpgrade = true;
	turret.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5d); // Is
												    // between
												    // 0
												    // and
												    // 1,
												    // so
												    // applying
												    // half
												    // resistance

	turret.movementSpeed = 0.25; // Slowed down
	turret.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(turret.movementSpeed); // Update
    }

    // Set by the packed up item (has already been crafted to have this
    // property)
    public static void applyMobilityUpgrade(Entity_AA turret)
    {
	turret.hasMobilityUpgrade = true; // You can move now
    }

    public static void applyWeaponUpgrade(Entity_AA turret)
    {
	turret.hasWeaponUpgrade = true; // Adding a second rail
    }

    public static void applyRidingUpgrade(Entity_AA turret)
    {
	turret.hasRidingUpgrade = true; // You can be ridden. Yeehaw!
    }

    public static void applyCommunicationUpgrade(Entity_AA turret)
    {
	turret.hasCommunicationUpgrade = true; // Chatty
    }

    // Either done in the field or saved by the packed up AA
    public static void applyNameTag(EntityPlayer player, Entity_AA turret, ItemStack stack, boolean consumeItem)
    {
	if (stack.hasDisplayName())
	{
	    turret.setCustomNameTag(stack.getDisplayName());
	} // Applying the name

	if (player.capabilities.isCreativeMode)
	{
	    return;
	} // Not deducting from creative mode players
	if (!consumeItem)
	{
	    return;
	} // Don't want me to consume this thing, so probably restoring
	  // properties from the packed up AA

	stack.shrink(1);
	// if (stack.getCount() <= 0) { player.setHeldItem(hand, stack); } //
	// Used up
    }

    // Only done in the field
    public static void doRepair(EntityPlayer player, Entity_AA turret, ItemStack stack)
    {
	if (turret.getHealth() >= turret.getMaxHealth())
	{
	    return;
	} // No repairs required

	turret.heal(20);

	// SFX

	NetHelper.sendParticleMessage(player, turret.getEntityId(), EnumParticleTypes.FIREWORKS_SPARK, (byte) 4); // Firework
														  // sparks
														  // particles
														  // (2)
	turret.playSound(SoundEvents.BLOCK_ANVIL_USE, 0.7f, 1.0f);

	if (player.capabilities.isCreativeMode)
	{
	    return;
	} // Not deducting from creative mode players

	stack.shrink(1);
	// if (player.getHeldItem().stackSize <= 0) {
	// player.setCurrentItemOrArmor(0, null); } // Used up
    }
}
