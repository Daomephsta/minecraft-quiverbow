package com.domochevsky.quiverbow;

import java.util.ArrayList;

import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles._ProjectileBase;
import com.domochevsky.quiverbow.recipes.*;
import com.domochevsky.quiverbow.weapons._WeaponBase;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Helper
{
    private static final ItemStack ARROW_STACK = new ItemStack(Items.ARROW);
    
    // Overhauled method for registering ammo (specifically, using magazines)
    public static void registerAmmoRecipe(Class<? extends _AmmoBase> ammoBase, Item weapon)
    {
	ArrayList<ItemStack> list = new ArrayList<ItemStack>();

	Item ammo = getAmmoByClass(ammoBase);

	ItemStack weaponStack = Helper.createEmptyWeaponOrAmmoStack(weapon, 1);
	ItemStack ammoStack = new ItemStack(ammo);

	list.add(weaponStack);
	list.add(ammoStack);

	GameRegistry.addRecipe(new RecipeLoadMagazine(ammo, weapon, list));
    }

    public static void registerAAUpgradeRecipe(ItemStack result, ItemStack[] input, String upgradeType)
    {
	if (upgradeType.equals("hasArmorUpgrade"))
	{
	    ArrayList<ItemStack> list = new ArrayList<ItemStack>();

	    int counter = 0;

	    while (counter < input.length)
	    {
		list.add(input[counter]);

		counter += 1;
	    }

	    GameRegistry.addRecipe(new Recipe_AA_Armor(result, list));
	}

	else if (upgradeType.equals("hasHeavyPlatingUpgrade"))
	{
	    ArrayList<ItemStack> list = new ArrayList<ItemStack>();

	    int counter = 0;

	    while (counter < input.length)
	    {
		list.add(input[counter]);

		counter += 1;
	    }

	    GameRegistry.addRecipe(new Recipe_AA_Plating(result, list));
	}

	else if (upgradeType.equals("hasMobilityUpgrade"))
	{
	    GameRegistry.addRecipe(new Recipe_AA_Mobility(3, 3, input, result));
	}

	else if (upgradeType.equals("hasStorageUpgrade"))
	{
	    GameRegistry.addRecipe(new Recipe_AA_Storage(3, 3, input, result));
	}

	else if (upgradeType.equals("hasWeaponUpgrade"))
	{
	    GameRegistry.addRecipe(new Recipe_AA_Weapon(3, 3, input, result));
	}

	else if (upgradeType.equals("hasRidingUpgrade"))
	{
	    ArrayList<ItemStack> list = new ArrayList<ItemStack>();

	    int counter = 0;

	    while (counter < input.length)
	    {
		list.add(input[counter]);

		counter += 1;
	    }

	    GameRegistry.addRecipe(new Recipe_AA_Riding(result, list));
	}

	else if (upgradeType.equals("hasCommunicationUpgrade"))
	{
	    ArrayList<ItemStack> list = new ArrayList<ItemStack>();

	    int counter = 0;

	    while (counter < input.length)
	    {
		list.add(input[counter]);

		counter += 1;
	    }

	    GameRegistry.addRecipe(new Recipe_AA_Communication(result, list));
	}
    }

    private static Item getAmmoByClass(Class<? extends _AmmoBase> targetClass)
    {
	for (_AmmoBase ammunition : Main.ammo)
	{
	    if (ammunition.getClass() == targetClass)
	    {
		return ammunition;
	    } // Found it
	}

	return null; // Don't have what you're looking for
    }

    public static ItemStack getAmmoStack(Class<? extends _AmmoBase> targetClass, int dmg)
    {
	for (_AmmoBase ammunition : Main.ammo)
	{
	    if (ammunition.getClass() == targetClass)
	    {
		return new ItemStack(ammunition, 1, dmg);
	    }
	}

	return null; // No idea what you're looking for
    }

    public static ItemStack getWeaponStackByClass(Class<? extends _WeaponBase> targetClass, boolean isEmpty)
    {
	for (_WeaponBase weapon : Main.weapons)
	{
	    if (weapon.getClass() == targetClass) // Found it
	    {
		if (isEmpty) // They want the empty version of this thing
		{
		    return Helper.createEmptyWeaponOrAmmoStack(weapon, 1);
		}
		else
		{
		    return new ItemStack(weapon);
		}
	    }
	}

	return null; // No idea what you want
    }

    // Kicks the passed in entity backwards, relative to the passed in strength
    // Needs to be done both on client and server, because the server doesn't
    // inform clients about small movement changes
    // This is the server-side part
    public static void knockUserBack(Entity user, byte strength)
    {
	user.motionZ += -MathHelper.cos((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);
	user.motionX += MathHelper.sin((user.rotationYaw) * (float) Math.PI / 180.0F) * (strength * 0.08F);

	NetHelper.sendKickbackMessage(user, strength); // Informing the client
						       // about this
    }

    // Sets the projectile to be pickupable depending on player creative mode
    // Used for throwable entities
    public static void setThrownPickup(EntityLivingBase entity, _ProjectileBase shot)
    {
	if (entity instanceof EntityPlayer) // Is a player
	{
	    // Creative mode?
	    EntityPlayer player = (EntityPlayer) entity;

	    if (player.capabilities.isCreativeMode)
	    {
		shot.canBePickedUp = false;
	    } // In creative mode, no drop
	    else
	    {
		shot.canBePickedUp = true;
	    } // Not in creative, so dropping is permitted

	}
	else
	{
	    shot.canBePickedUp = false;
	} // Not a player, so not dropping anything
    }

    // Unified appliance of potion effects
    public static void applyPotionEffect(EntityLivingBase entitylivingbase, PotionEffect effect)
    {
	if (entitylivingbase == null)
	{
	    return;
	} // Not a valid entity, for some reason

	if (effect == null)
	{
	    return;
	} // Nothing to apply

	PotionEffect potion = entitylivingbase.getActivePotionEffect(effect.getPotion());
	if (potion != null) // Already exists. Extending it
	{
	    int dur = potion.getDuration();

	    entitylivingbase.addPotionEffect(
		    new PotionEffect(effect.getPotion(), effect.getDuration() + dur, effect.getAmplifier() - 1));
	}
	else
	{
	    entitylivingbase.addPotionEffect(
		    new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier() - 1));
	} // Fresh
    }

    // Time to make a mess!
    // Checking if the block hit can be broken
    // stronger weapons can break more block types
    public static boolean tryBlockBreak(World world, Entity entity, RayTraceResult target, int strength)
    {
	if (!Main.breakGlass)
	{
	    return false;
	} // Not allowed to break anything in general

	if (entity instanceof _ProjectileBase)
	{
	    _ProjectileBase projectile = (_ProjectileBase) entity;

	    if (projectile.shootingEntity != null && !(projectile.shootingEntity instanceof EntityPlayer))
	    {
		// Not shot by a player, so checking for mob griefing
		if (!world.getGameRules().getBoolean("mobGriefing"))
		{
		    return false;
		} // Not allowed to break things
	    }
	}

	IBlockState state = world.getBlockState(target.getBlockPos());
	if (state == Blocks.AIR.getDefaultState())
	{
	    return false;
	} // Didn't hit a valid block? Do we continue? Stop?

	boolean breakThis = false;

	if (strength >= 0) // Weak stuff
	{
	    if (state.getMaterial() == Material.CAKE || state.getMaterial() == Material.GOURD)
	    {
		breakThis = true;
	    }
	}

	if (strength >= 1) // Medium stuff
	{
	    if (state.getMaterial() == Material.GLASS || state.getMaterial() == Material.WEB || state == Blocks.TORCH
		    || state == Blocks.FLOWER_POT) // Hit something made of
						   // glass. Breaking it!
	    {
		breakThis = true;
	    }
	}

	if (strength >= 2) // Strong stuff
	{
	    if (state.getMaterial() == Material.LEAVES || state.getMaterial() == Material.ICE) breakThis = true;
	}

	if (strength >= 3) // Super strong stuff
	{
	    breakThis = true; // Default breakage, then negating what doesn't
			      // work

	    if (state instanceof BlockLiquid || state.getMaterial() == Material.PORTAL || state == Blocks.MOB_SPAWNER
		    || state == Blocks.BEDROCK || state == Blocks.OBSIDIAN)
		breakThis = false;
	}

	if (state == Blocks.BEACON)
	{
	    breakThis = false;
	} // ...beacons are made out of glass, too. Not breaking those.

	if (breakThis) // Breaking? Breaking!
	{
	    if (Main.sendBlockBreak)
	    {
		if (entity instanceof _ProjectileBase)
		{
		    _ProjectileBase projectile = (_ProjectileBase) entity;

		    // If you were shot by a player, are they allowed to break
		    // this block?
		    Entity shooter = projectile.getShooter();

		    if (shooter instanceof EntityPlayerMP)
		    {
			GameType gametype = world.getWorldInfo().getGameType();
			int result = ForgeHooks.onBlockBreakEvent(world, gametype, (EntityPlayerMP) shooter,
				target.getBlockPos());

			if (result == -1)
			{
			    return false;
			} // Not allowed to do this
		    }
		}
		else if (entity instanceof EntityPlayerMP)
		{
		    GameType gametype = entity.world.getWorldInfo().getGameType();
		    int result = ForgeHooks.onBlockBreakEvent(entity.world, gametype, (EntityPlayerMP) entity,
			    target.getBlockPos());

		    if (result == -1)
		    {
			return false;
		    } // Not allowed to do this
		}
	    }
	    // else, not interested in sending such a event, so whatever

	    world.destroyBlock(target.getBlockPos(), true);

	    return true; // Successfully broken
	}

	return false; // Couldn't break whatever's there
    }

    // Does the weapon have a custom name or other upgrades? If so then we're
    // transfering that to the new item
    public static void copyProps(IInventory craftMatrix, ItemStack newItem)
    {
	// Step 1, find the actual item (It's possible that this is not a
	// reloading action, meaning there is no weapon to copy the name from)

	int slot = 0;

	while (slot < 9)
	{
	    ItemStack stack = craftMatrix.getStackInSlot(slot);

	    if (stack != null && stack.getItem() instanceof _WeaponBase) // Found
									 // it.
									 // Does
									 // it
									 // have
									 // a
									 // name
									 // tag?
	    {
		if (stack.hasDisplayName() && !newItem.hasDisplayName())
		{
		    newItem.setStackDisplayName(stack.getDisplayName());
		}
		// else, has no custom display name or the new item already has
		// one. Fine with me either way.

		// Upgrades
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle"))
		{
		    if (!newItem.hasTagCompound())
		    {
			newItem.setTagCompound(new NBTTagCompound());
		    } // Init
		    newItem.getTagCompound().setBoolean("hasEmeraldMuzzle", true); // Keeping
										   // the
										   // upgrade
		}

		return; // Either way, we're done here
	    }
	    // else, either doesn't exist or not what I'm looking for

	    slot += 1;
	}
    }

    public static boolean canEntityBeSeen(World world, Entity observer, Entity entity)
    {
	return world.rayTraceBlocks(
		new Vec3d(observer.posX, observer.posY + (double) observer.getEyeHeight(), observer.posZ),
		new Vec3d(entity.posX, entity.posY + (double) entity.getEyeHeight(), entity.posZ)) == null;
    }
    
    public static ItemStack createEmptyWeaponOrAmmoStack(Item item, int count)
    {
	ItemStack stack = new ItemStack(item, count);
	stack.setItemDamage(stack.getMaxDamage());
	return stack;
    }

    public static void playSoundAtEntityPos(Entity entity, SoundEvent sound, float volume, float pitch)
    {
        entity.world.playSound(entity.posX, entity.posY, entity.posZ, sound, entity.getSoundCategory(), volume, pitch,
        	false);
    }

    public static EntityArrow createArrow(World world, EntityLivingBase shooter)
    {
        return ((ItemArrow) Items.ARROW).createArrow(world, ARROW_STACK, shooter);
    }
}
