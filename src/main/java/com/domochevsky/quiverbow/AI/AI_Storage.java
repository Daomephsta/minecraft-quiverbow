package com.domochevsky.quiverbow.AI;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;

public class AI_Storage
{
    public static void addItem(EntityPlayer player, Entity_AA turret, ItemStack playerStack, EnumHand hand)
    {
	int slot = 0;

	while (slot < turret.storage.length)
	{
	    if (turret.storage[slot] == null) // That spot is free
	    {
		turret.storage[slot] = playerStack.copy(); // Stored
		if (!player.capabilities.isCreativeMode)
		{
		    player.setHeldItem(hand, ItemStack.EMPTY);
		} // Empty

		if (playerStack.getItem() == Items.WRITABLE_BOOK
			&& AI_Targeting.isNameOnWhitelist(turret, Commands.cmdStayStationary))
		{
		    // System.out.println("[ARMS ASSISTANT] Received a book with
		    // STAY command. Setting target position");
		    turret.stationaryX = turret.posX;
		    turret.stationaryY = turret.posY;
		    turret.stationaryZ = turret.posZ;
		}

		// Informing the client about this change
		NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret,
			Item.getIdFromItem(turret.storage[slot].getItem()), slot, turret.storage[slot].getItemDamage());

		return; // We're done here
	    }
	    // else, there's something in there

	    slot += 1;
	}

	// No free spot found. What about existing ones?
	slot = 0;

	while (slot < turret.storage.length)
	{
	    if (turret.storage[slot] != null)
	    {
		boolean skip = false;

		if (!(turret.storage[slot].getItem() instanceof _AmmoBase))
		{
		    skip = true;
		} // Not ammunition, hm?

		if (turret.storage[slot].getItemDamage() < turret.storage[slot].getMaxDamage())
		{
		    skip = true;
		} // Not empty

		if (!skip)
		{
		    // Has an empty magazine in there, so replacing that now
		    dropSingleItem(turret, turret.storage[slot].copy());
		    turret.storage[slot] = playerStack.copy(); // Stored

		    if (!player.capabilities.isCreativeMode)
		    {
			player.setHeldItem(hand, ItemStack.EMPTY);
		    } // Empty

		    // Informing the client about this change
		    NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret,
			    Item.getIdFromItem(turret.storage[slot].getItem()), slot,
			    turret.storage[slot].getItemDamage());
		    return;
		}
		// else, not a magazine
	    }
	    // else, no free spot found but this is null? Da fuq?

	    slot += 1;
	}
    }

    // Packing myself up and dropping as the spawner item
    public static void dropSelf(Entity_AA turret)
    {
	ItemStack spawner = new ItemStack(Main.packedAA);

	// Adding some customization data
	spawner.setTagCompound(new NBTTagCompound());

	// Saving the current health
	spawner.getTagCompound().setInteger("currentHealth", (int) turret.getHealth());

	// Saving upgrades
	spawner.getTagCompound().setBoolean("hasArmorUpgrade", turret.hasArmorUpgrade);
	spawner.getTagCompound().setBoolean("hasMobilityUpgrade", turret.hasMobilityUpgrade);
	spawner.getTagCompound().setBoolean("hasStorageUpgrade", turret.hasStorageUpgrade);
	spawner.getTagCompound().setBoolean("hasWeaponUpgrade", turret.hasWeaponUpgrade);
	spawner.getTagCompound().setBoolean("hasRidingUpgrade", turret.hasRidingUpgrade);
	spawner.getTagCompound().setBoolean("hasHeavyPlatingUpgrade", turret.hasHeavyPlatingUpgrade);
	spawner.getTagCompound().setBoolean("hasCommunicationUpgrade", turret.hasCommunicationUpgrade);

	// Saving the name
	if (turret.hasCustomName())
	{
	    spawner.setStackDisplayName(turret.getCustomNameTag());
	}

	dropSingleItem(turret, spawner); // Drop the packed up AA
	dropStoredItems(turret); // Spill your items, too

	// SFX
	turret.playSound(SoundEvents.BLOCK_METAL_BREAK, 1.0F, 0.2F);

	turret.setDead();
    }

    public static void dropStoredItems(Entity_AA turret)
    {
	if (turret.ownerName.equals("Herobrine"))
	{
	    return;
	} // Nope.

	int slot = 0;

	while (slot < turret.storage.length)
	{
	    if (turret.storage[slot] != null) // Dumping
	    {
		dropSingleItem(turret, turret.storage[slot]);
		turret.storage[slot] = null;

		// Informing the client about this change
		NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.world, turret, -1, slot, 0);
	    }

	    slot += 1;
	}
    }

    public static void dropFirstWeapon(Entity_AA turret)
    {
	// Validation
	if (turret.getHeldItem(EnumHand.MAIN_HAND) == null)
	{
	    return;
	}
	if (turret.firstWeapon == null)
	{
	    return;
	}

	if (turret.ownerName.equals("Herobrine"))
	{
	    return;
	} // Nope.

	// Primary weapon
	dropSingleItem(turret, turret.getHeldItem(EnumHand.MAIN_HAND));

	turret.firstWeapon = null;
	turret.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);

	turret.hasFirstWeapon = false;
    }

    public static void dropSecondWeapon(Entity_AA turret)
    {
	// Validation
	if (turret.getHeldItem(EnumHand.OFF_HAND) == null)
	{
	    return;
	}
	if (turret.secondWeapon == null)
	{
	    return;
	}

	if (turret.ownerName.equals("Herobrine"))
	{
	    return;
	} // Nope.

	// Secondary weapon
	dropSingleItem(turret, turret.getHeldItem(EnumHand.OFF_HAND));
	turret.secondWeapon = null;
	turret.setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);

	turret.hasSecondWeapon = false;
    }

    // I was destroyed, so dropping my parts now
    public static void dropParts(Entity_AA turret)
    {
	if (turret.ownerName.equals("Herobrine"))
	{
	    return;
	} // Nope.

	// Dropping the wither skull and other parts (but not everything.
	// There's some loss)
	dropSingleItem(turret, new ItemStack(Items.SKULL, 1, 1));
	dropSingleItem(turret, new ItemStack(Items.ENDER_EYE, 2));
	dropSingleItem(turret, new ItemStack(Items.IRON_INGOT, 4));
	dropSingleItem(turret, new ItemStack(Blocks.STICKY_PISTON, 2));

	// Dropping stuff that went into the making of this
	if (turret.hasArmorUpgrade)
	{
	    dropSingleItem(turret, new ItemStack(Items.DIAMOND, 4));
	}

	if (turret.hasMobilityUpgrade)
	{
	    dropSingleItem(turret, new ItemStack(Item.getItemFromBlock(Blocks.IRON_BARS), 1));
	    dropSingleItem(turret, new ItemStack(Item.getItemFromBlock(Blocks.STICKY_PISTON), 1));
	}

	if (turret.hasStorageUpgrade)
	{
	    dropSingleItem(turret, new ItemStack(Item.getItemFromBlock(Blocks.PLANKS), 4));
	    dropSingleItem(turret, new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN)));
	    dropSingleItem(turret, new ItemStack(Items.SLIME_BALL, 1));
	}

	if (turret.hasRidingUpgrade)
	{
	    dropSingleItem(turret, new ItemStack(Items.SADDLE));
	}
    }

    public static void dropSingleItem(Entity_AA turret, ItemStack stack)
    {
	EntityItem entityitem = new EntityItem(turret.world, turret.posX, turret.posY + 1.0d, turret.posZ, stack);
	entityitem.setDefaultPickupDelay();

	// And dropping it
	turret.world.spawnEntity(entityitem);
    }
}
