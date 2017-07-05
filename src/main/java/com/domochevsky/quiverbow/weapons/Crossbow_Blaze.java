package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.BlazeShot;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Crossbow_Blaze extends _WeaponBase
{
    public Crossbow_Blaze()
    {
	super("blaze_crossbow", 1);
    }

    private int FireDur;

    /*
     * @SideOnly(Side.CLIENT)
     * 
     * @Override public void registerIcons(IIconRegister par1IconRegister) {
     * this.Icon =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowBlaze");
     * this.Icon_Empty =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowBlaze_Empty"
     * ); }
     */

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= stack.getMaxDamage())
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Is empty

	this.doSingleFire(stack, world, player); // Handing it over to the
						 // neutral firing function
	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void doSingleFire(ItemStack stack, World world, Entity entity) // Server
									  // side
    {
	if (this.getCooldown(stack) != 0)
	{
	    return;
	} // Hasn't cooled down yet

	// SFX
	Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.5F);

	// Firing
	BlazeShot entityarrow = new BlazeShot(world, entity, (float) this.Speed);

	// Random Damage
	int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
						   // is 10, then the range will
						   // be 10
	int dmg = world.rand.nextInt(dmg_range + 1);// Range will be between 0
						    // and 10
	dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
			    // the proper damage range (10-20)

	entityarrow.damage = dmg;
	entityarrow.knockbackStrength = this.Knockback; // Comes with an inbuild
							// knockback II
	entityarrow.fireDuration = this.FireDur;
	entityarrow.ticksInGroundMax = 200; // 200 ticks for 10 sec

	world.spawnEntity(entityarrow); // pew

	// SFX
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F);
	world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY + 0.5D, entity.posZ, 0.0D, 0.0D,
		0.0D);

	this.consumeAmmo(stack, entity, 1);
	this.setCooldown(stack, 10);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 20)", 20).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 30)", 30).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0)
		.getDouble();
	this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
		.getInt();

	this.FireDur = config.get(this.name, "How long is the target on fire? (default 15 sec)", 15).getInt();
	config.get(this.name, "How long do I keep burning when stuck in the ground? (default 10 sec)", 10).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One blaze crossbow (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "bib", "ici", "bib", 'b',
		    Items.BLAZE_POWDER, 'i', Items.IRON_INGOT, 'c',
		    Helper.getWeaponStackByClass(Crossbow_Compact.class, true));
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addShapelessRecipe(new ItemStack(this), // Fill the empty
							     // blaze crossbow
							     // with one rod
		Items.BLAZE_ROD, Helper.createEmptyWeaponOrAmmoStack(this, 1));
    }
}
