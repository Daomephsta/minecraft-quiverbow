package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderAccelerator;
import com.domochevsky.quiverbow.recipes.Recipe_ERA;
import com.domochevsky.quiverbow.recipes.Recipe_Weapon;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.FiringBehaviourBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ERA extends _WeaponBase
{
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
	{
	}
    }

    private double explosionSelf;
    public double explosionTarget;
    private boolean dmgTerrain; // Can our projectile damage terrain?

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
		Helper.knockUserBack(entity, this.Kickback); // Kickback

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
		    world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, (float) this.explosionTarget,
			    this.dmgTerrain); // Big baddaboom

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
		    damageTerrain = this.dmgTerrain;
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
		    world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, (float) this.explosionSelf,
			    damageTerrain); // Hurtin' more
		}

		if(!world.isRemote)
		{
		    // Spawn projectile and go
		    EnderAccelerator shot = new EnderAccelerator(world, entity, 5.0f);

		    // Random Damage
		    int dmg_range = DmgMax - DmgMin; // If max dmg is 20 and min is
		    // 10, then the range will be
		    // 10
		    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
		    // between 0 and 10
		    dmg += DmgMin; // Adding the min dmg of 10 back on top, giving
		    // us the proper damage range (10-20)

		    shot.damage = dmg;
		    shot.ticksInAirMax = 120; // 6 sec?
		    shot.damageTerrain = damageTerrain;
		    shot.explosionSize = (float) this.explosionTarget;

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
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing with a direct hit, at least? (default 120)", 120)
		.getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing with a direct hit, tops? (default 150)", 150)
		.getInt();

	this.explosionSelf = config.get(this.name,
		"How big are my explosions when leaving the barrel? (default 4.0 blocks. TNT is 4.0 blocks)", 4.0)
		.getDouble();
	this.explosionTarget = config
		.get(this.name,
			"How big are my explosions when hitting a target? (default 8.0 blocks. TNT is 4.0 blocks)", 8.0)
		.getDouble();

	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 30)", 30)
		.getInt();

	this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
		.getBoolean(true);

	this.isMobUsable = config
		.get(this.name, "Can I be used by QuiverMobs? (default false. Too high-power and suicidal.)", false)
		.getBoolean();
    }

    @Override
    public void addRecipes()
    {
	if (Enabled)
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
    }

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

	GameRegistry.addRecipe(new Recipe_ERA(input, new ItemStack(this)));
    }

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

	GameRegistry.addRecipe(new Recipe_ERA(repair, new ItemStack(this)));
    }

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

	GameRegistry.addRecipe(new Recipe_Weapon(recipe, new ItemStack(this), 1)); // Emerald
	// Muzzle
    }
}
