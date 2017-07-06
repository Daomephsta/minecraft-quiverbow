package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.projectiles.RegularArrow;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Crossbow_AutoImp extends _WeaponBase
{
    public Crossbow_AutoImp()
    {
	super("auto_crossbow_imp", 16);
    } // 2 bundles

    private String name = "Improved Auto-Crossbow";

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

	if(!world.isRemote)
	{
	    RegularArrow entityarrow = new RegularArrow(world, entity, (float) this.Speed);

	    // Random Damage
	    int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	    // and 10
	    dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)

	    entityarrow.damage = dmg;
	    entityarrow.knockbackStrength = this.Knockback; // Comes with an inbuild
	    // knockback I

	    world.spawnEntity(entityarrow); // pew
	}

	this.consumeAmmo(stack, entity, 1);
	this.setCooldown(stack, this.Cooldown);
	// Auto-rechambering
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 10)", 10).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 16)", 16).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
		.getDouble();
	this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 1)", 1)
		.getInt();

	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 8 ticks)", 8).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false.)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One auto-crossbow (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "iii", "scs", " i ", 'i',
		    Items.IRON_INGOT, 's', Blocks.STICKY_PISTON, 'c',
		    Helper.getWeaponStackByClass(Crossbow_Auto.class, true));
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	ItemStack ammo = Helper.getAmmoStack(ArrowBundle.class, 0);

	// Fill what can be filled. One arrow bundle for 8 shots, for up to 2
	// bundles
	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(ammo.getItem(), 8, 1, 2));
    }
}
