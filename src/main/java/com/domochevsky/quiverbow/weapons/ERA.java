package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderAccelerator;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.FiringBehaviourBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ERA extends WeaponBase
{
	private static final String PROP_SELF_EXPLOSION_SIZE = "selfExplosionSize";
	private class ERAFiringBehaviour extends FiringBehaviourBase<ERA>
	{
		protected ERAFiringBehaviour()
		{
			super(ERA.this);
		}

		@Override
		public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
		{
			if (ERA.isAccelerating(stack))
			{
				return;
			} // Already in the middle of firing

			// Firing
			ERA.startAccelerating(stack);
		}

		@Override
		public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
		{}
	}

	public ERA()
	{
		super("ender_rail_accelerator", 1);
		setFiringBehaviour(new ERAFiringBehaviour());
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
	{
		// Used for ticking up 27 * 2 times (with increasing pitch) after
		// triggered and before firing
		// 54 ticks minimum per shot (movement in/out)

		if (ERA.isAccelerating(stack))
		{
			stack.getTagCompound().setInteger("acceleration", stack.getTagCompound().getInteger("acceleration") - 1); // Ticking
			// down
			stack.getTagCompound().setFloat("accSFX", stack.getTagCompound().getFloat("accSFX") + 0.02f); // And
			// pitching
			// up

			Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
					stack.getTagCompound().getFloat("accSFX"), stack.getTagCompound().getFloat("accSFX"));
			// mob.endermen.portal
			// mob.enderdragon.wings

			if (stack.getTagCompound().getInteger("acceleration") <= 0) // Ready
			// to
			// fire
			{
				Helper.knockUserBack(entity, this.getKickback()); // Kickback

				// Upgrade
				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle"))
				{
					entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 15.0f); // Hurtin'
					// (but
					// less
					// so)
				}
				else
				{
					entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 20.0f); // Hurtin'
				}

				boolean damageTerrain = world.getGameRules().getBoolean("mobGriefing");

				if (!holdingItem) // Isn't holding the weapon, so this is gonna
				// go off in their pockets
				{
					entity.hurtResistantTime = 0; // No rest for the wicked
					world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, getProperties().getFloat(CommonProperties.PROP_EXPLOSION_SIZE),
							getProperties().getBoolean(CommonProperties.PROP_DAMAGE_TERRAIN)); // Big baddaboom

					// Set weapon to "burnt out" (if the user's a player and not
					// in creative mode)
					if (entity instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) entity;

						if (player.capabilities.isCreativeMode)
						{} // Is in creative mode, so not burning out
						else
						{
							stack.setItemDamage(1);
						}
					}
					// else, not a player. Not burning out

					return; // We're done here
				}

				if (entity instanceof EntityPlayer)
				{
					damageTerrain = getProperties().getBoolean(CommonProperties.PROP_DAMAGE_TERRAIN);
				} // Players don't care about mob griefing rules, but play by
					// their own rules

				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle"))
				{
					// Has a muzzle, so no boom
					Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 2.0F, 0.1F);
					NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(),
							EnumParticleTypes.SMOKE_LARGE, (byte) 6);
				}
				else
				{
					world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, getProperties().getFloat(PROP_SELF_EXPLOSION_SIZE),
							damageTerrain); // Hurtin' more
				}

				if (!world.isRemote)
				{
					// Spawn projectile and go
					EnderAccelerator shot = new EnderAccelerator(world, entity, 5.0f);

					// Random Damage
					int dmg_range = getProperties().getDamageMin() - getProperties().getDamageMin(); // If max dmg is 20 and min
														// is
					// 10, then the range will be
					// 10
					int dmg = world.rand.nextInt(dmg_range + 1); // Range will
																	// be
					// between 0 and 10
					dmg += getProperties().getDamageMin(); // Adding the min dmg of 10 back on top,
									// giving
					// us the proper damage range (10-20)

					shot.damage = dmg;
					shot.ticksInAirMax = 120; // 6 sec?
					shot.damageTerrain = damageTerrain;
					shot.explosionSize = getProperties().getFloat(PROP_SELF_EXPLOSION_SIZE);

					world.spawnEntity(shot);
				}

				// Set weapon to "burnt out" (if the user's a player and not in
				// creative mode)
				if (entity instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer) entity;

					if (player.capabilities.isCreativeMode)
					{} // Is in creative mode, so not burning out
					else
					{
						stack.setItemDamage(1);
					}
				}
				// else, not a player. Not burning out
			}
			// else, not ready yet
		}
		// else, all's chill
	}

	private static void startAccelerating(ItemStack stack)
	{
		if (stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}

		stack.getTagCompound().setInteger("acceleration", 54);
		stack.getTagCompound().setFloat("accSFX", 0.02f);
	}

	private static boolean isAccelerating(ItemStack stack)
	{
		if (stack.getTagCompound() == null)
		{
			return false;
		}

		if (stack.getTagCompound().getInteger("acceleration") <= 0)
		{
			return false;
		} // If this is higher than 0 then it's currently counting down to the
			// moment it fires

		return true; // Seems to check out
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(120).maximumDamage(150).kickback(30)
				.floatProperty(PROP_SELF_EXPLOSION_SIZE,
						"How large the explosion at the user location is in blocks. A TNT explosion is 4.0 blocks",
						4.0F)
				.floatProperty(CommonProperties.PROP_EXPLOSION_SIZE, CommonProperties.COMMENT_EXPLOSION_SIZE, 8.0F)
				.booleanProperty(CommonProperties.PROP_DAMAGE_TERRAIN, CommonProperties.COMMENT_DAMAGE_TERRAIN, true)
				.build();
	}

	//TODO Convert to JSON
/*	public void addRecipes()
	{
		if (enabled)
		{
			this.registerRecipe();
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		// Reloading? More "repairing" the burnt out one

		this.registerRepair();
		this.registerUpgrade();
	}*/
	
	//TODO Convert to JSON
	private void registerRecipe()
	{
		// Fully loaded
		// Alternate item registering method
		ItemStack[] input = new ItemStack[9];

		// Top row
		input[0] = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));
		input[1] = new ItemStack(Blocks.GOLDEN_RAIL, 27); // 27 rails
		input[2] = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));

		// Middle row
		input[3] = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));
		input[4] = new ItemStack(Item.getItemFromBlock(Blocks.ENDER_CHEST));
		input[5] = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));

		// Bottom row
		input[6] = new ItemStack(Item.getItemFromBlock(Blocks.TRIPWIRE_HOOK));
		input[7] = new ItemStack(Items.IRON_INGOT);
		input[8] = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));

//		GameRegistry.addRecipe(new RecipeERA(input, new ItemStack(this)));
	}
	
	//TODO Convert to JSON
	private void registerRepair()
	{
		ItemStack[] repair = new ItemStack[9];

		// Top row
		// repair[0] = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));
		repair[1] = new ItemStack(Blocks.GOLDEN_RAIL);
		// repair[2] = new ItemStack(Item.getItemFromBlock(Blocks.OBSIDIAN));

		// Middle row
		repair[3] = new ItemStack(Blocks.GOLDEN_RAIL);
		repair[4] = Helper.createEmptyWeaponOrAmmoStack(this, 1);
		repair[5] = new ItemStack(Blocks.GOLDEN_RAIL);

		// Bottom row
		repair[6] = new ItemStack(Items.REDSTONE);
		repair[7] = new ItemStack(Items.IRON_INGOT);
		repair[8] = new ItemStack(Items.REDSTONE);

//		GameRegistry.addRecipe(new RecipeERA(repair, new ItemStack(this)));
	}

	//TODO Convert to JSON
	private void registerUpgrade()
	{
		ItemStack[] recipe = new ItemStack[9];

		// Top row
		recipe[0] = new ItemStack(Blocks.QUARTZ_BLOCK); // 0 1 2
		recipe[1] = new ItemStack(Items.EMERALD); // - - -
		// recipe[2] = null; // - - -

		// Middle row
		recipe[3] = new ItemStack(Blocks.EMERALD_BLOCK); // - - -
		// recipe[4] = null; // 3 4 5
		recipe[5] = new ItemStack(Items.EMERALD); // - - -

		// Bottom row
		recipe[6] = new ItemStack(this); // - - -
		recipe[7] = new ItemStack(Blocks.EMERALD_BLOCK); // - - -
		recipe[8] = new ItemStack(Blocks.QUARTZ_BLOCK); // 6 7 8

//		GameRegistry.addRecipe(new RecipeWeapon(recipe, new ItemStack(this), 1)); // Emerald
		// Muzzle
	}
}
