package com.domochevsky.quiverbow.weapons;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.Configuration;
import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PowderKnuckle_Mod extends _WeaponBase
{
    public PowderKnuckle_Mod()
    {
	super("powder_knuckle_mod", 8);
    }

    private double ExplosionSize;

    private boolean dmgTerrain;

    /*
     * @SideOnly(Side.CLIENT)
     * 
     * @Override public void registerIcons(IIconRegister par1IconRegister) {
     * this.Icon = par1IconRegister.registerIcon(
     * "quiverchevsky:weapons/PowderKnuckle_Modified"); this.Icon_Empty =
     * par1IconRegister.registerIcon(
     * "quiverchevsky:weapons/PowderKnuckle_Modified_Empty"); }
     */

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
	    float hitX, float hitY, float hitZ)
    {
	ItemStack stack = player.getHeldItem(hand);
	// Right click
	if (this.getDamage(stack) >= stack.getMaxDamage())
	{
	    return EnumActionResult.FAIL;
	} // Not loaded

	this.consumeAmmo(stack, player, 1);

	// SFX
	NetHelper.sendParticleMessageToAllPlayers(world, player.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
		(byte) 4); // smoke

	// Dmg
	world.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), (float) this.ExplosionSize, true); // 4.0F
	// is
	// TNT

	// Mining
	for (int xAxis = -1; xAxis <= 1; xAxis++) // Along the x axis
	{
	    for (int yAxis = -1; yAxis <= 1; yAxis++) // Along the y axis
	    {
		for (int zAxis = -1; zAxis <= 1; zAxis++) // Along the z axis
		{
		    this.doMining(world, (EntityPlayerMP) player, pos.add(xAxis, yAxis, zAxis)); // That
												 // should
												 // give
												 // me
												 // 3
												 // iterations
												 // of
												 // each
												 // axis
												 // on
												 // every
												 // level
		}
	    }
	}

	return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
	if (player.world.isRemote)
	{
	    return false;
	} // Not doing this on client side

	if (this.getDamage(stack) >= stack.getMaxDamage())
	{
	    entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.DmgMin);
	    entity.hurtResistantTime = 0; // No invincibility frames

	    return false; // We're not loaded, getting out of here with minimal
			  // damage
	}

	this.consumeAmmo(stack, entity, 1);

	// SFX
	NetHelper.sendParticleMessageToAllPlayers(entity.world, player.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
		(byte) 4); // smoke

	// Dmg
	entity.world.createExplosion(player, entity.posX, entity.posY + 0.5D, entity.posZ, (float) this.ExplosionSize,
		this.dmgTerrain); // 4.0F is TNT
	entity.setFire(2); // Setting fire to them for 2 sec, so pigs can drop
			   // cooked porkchops

	entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.DmgMax); // Dealing
										      // damage
										      // directly.
										      // Screw
										      // weapon
										      // attributes

	return false;
    }

    void doMining(World world, EntityPlayerMP player, BlockPos pos) // Calling
								    // this
								    // 27
								    // times,
								    // to
								    // blast
								    // mine
								    // a
								    // 3x3x3
								    // area
    {
	IBlockState toBeBroken = world.getBlockState(pos);

	if (toBeBroken.getBlockHardness(world, pos) == -1)
	{
	    return;
	} // Unbreakable

	if (toBeBroken.getBlock().getHarvestLevel(toBeBroken) > 1)
	{
	    return;
	}
	if (toBeBroken.getMaterial() == Material.WATER)
	{
	    return;
	}
	if (toBeBroken.getMaterial() == Material.LAVA)
	{
	    return;
	}
	if (toBeBroken.getMaterial() == Material.AIR)
	{
	    return;
	}
	if (toBeBroken.getMaterial() == Material.PORTAL)
	{
	    return;
	}

	// Need to do checks here against invalid blocks
	if (toBeBroken == Blocks.WATER)
	{
	    return;
	}
	if (toBeBroken == Blocks.FLOWING_WATER)
	{
	    return;
	}
	if (toBeBroken == Blocks.LAVA)
	{
	    return;
	}
	if (toBeBroken == Blocks.FLOWING_LAVA)
	{
	    return;
	}
	if (toBeBroken == Blocks.OBSIDIAN)
	{
	    return;
	}
	if (toBeBroken == Blocks.MOB_SPAWNER)
	{
	    return;
	}
	GameType gametype = world.getWorldInfo().getGameType();
	int result = ForgeHooks.onBlockBreakEvent(world, gametype, player, pos);
	if (result == -1)
	{
	    return;
	} // Not allowed to do this
	world.destroyBlock(pos, true);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What's my minimum damage, when I'm empty? (default 2)", 2).getInt();
	this.DmgMax = config.get(this.name, "What's my maximum damage when I explode? (default 14)", 14).getInt();

	this.ExplosionSize = config
		.get(this.name, "How big are my explosions? (default 1.5 blocks. TNT is 4.0 blocks)", 1.5).getDouble();
	this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true)
		.getBoolean(true);

	this.isMobUsable = config.get(this.name,
		"Can I be used by QuiverMobs? (default false. They don't know where the trigger on this thing is.)",
		false).getBoolean(false);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // Modifying the powder knuckle once
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "ooo", "oco", "i i", 'c',
		    Helper.getWeaponStackByClass(PowderKnuckle.class, true), 'o', Blocks.OBSIDIAN, 'i',
		    Items.IRON_INGOT);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(Items.GUNPOWDER, 1));
    }
}
