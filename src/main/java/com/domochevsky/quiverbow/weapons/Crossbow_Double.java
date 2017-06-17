package com.domochevsky.quiverbow.weapons;

import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.RegularArrow;
import com.domochevsky.quiverbow.util.Newliner;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Crossbow_Double extends _WeaponBase
{
    public Crossbow_Double()
    {
	super("double_crossbow", 2);
    }

    // Icons
    /*@SideOnly(Side.CLIENT)
    public IIcon Icon_Half;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
	this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowDouble");
	this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowDouble_Empty");
	this.Icon_Half = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowDouble_Half");
    }*/

   /* @Override
    public IIcon getIcon(ItemStack stack, int pass)
    {
	if (this.getDamage(stack) >= this.getMaxDamage())
	{
	    return this.Icon_Empty;
	} // Empty
	if (this.getDamage(stack) == 1)
	{
	    return this.Icon_Half;
	} // One arrow on the bay

	return this.Icon;
    }
    // This is for on-hand display. Only gets called on client side. Ideally
    // won't get used at all once models are fully integrated

    @Override
    public IIcon getIconFromDamage(int meta) // This is for inventory display.
					     // Comes in with metadata. Only
					     // gets called on client side
    {
	if (meta == this.getMaxDamage())
	{
	    return this.Icon_Empty;
	} // Empty
	if (meta == 1)
	{
	    return this.Icon_Half;
	}

	return this.Icon; // Full, default
    }*/

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= this.getMaxDamage())
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
	Utils.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 0.5F);

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
							// knockback II

	world.spawnEntity(entityarrow); // pew

	this.consumeAmmo(stack, entity, 1);
	this.setCooldown(stack, this.Cooldown);
    }

    @Override
    void doCooldownSFX(World world, Entity entity) // Server side
    {
	Utils.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.5F, 0.4F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
	super.addInformation(stack, player, list, par4);
	if (this.getCooldown(stack) > 0) Collections.addAll(list, Newliner
		.translateAndParse(getUnlocalizedName() + ".cooldown", this.displayInSec(this.getCooldown(stack))));
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 14)", 14).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 20)", 20).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
		.getDouble();
	this.Knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 2)", 2)
		.getInt();
	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 25 ticks)", 25).getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One empty double crossbow (upgraded from regular crossbow)
	    GameRegistry.addShapelessRecipe(new ItemStack(this, 1, this.getMaxDamage()), Blocks.STICKY_PISTON,
		    Items.REPEATER, Helper.getWeaponStackByClass(Crossbow_Compact.class, true));
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addShapelessRecipe(new ItemStack(this), // Fill the empty
							     // crossbow with
							     // two arrows
		Items.ARROW, Items.ARROW, new ItemStack(this, 1, this.getMaxDamage()));

	GameRegistry.addShapelessRecipe(new ItemStack(this, 1, 1), // Fill the
								   // empty
								   // crossbow
								   // with one
								   // arrow
		Items.ARROW, new ItemStack(this, 1, this.getMaxDamage()));

	GameRegistry.addShapelessRecipe(new ItemStack(this), // Fill the half
							     // empty crossbow
							     // with one arrow
		Items.ARROW, new ItemStack(this, 1, 1));
    }

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	if (stack.getItemDamage() == 1)
	{
	    return "CrossbowDouble_half";
	} // One arrow gone
	if (stack.getItemDamage() >= stack.getMaxDamage())
	{
	    return "CrossbowDouble_empty";
	} // empty

	return "CrossbowDouble"; // Regular
    }
}
