package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.projectiles.RegularArrow;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Crossbow_Auto extends _WeaponBase
{
    public Crossbow_Auto()
    {
	super("auto_crossbow", 8);
    }

    /*@SideOnly(Side.CLIENT)
    public IIcon Icon_Unchambered; // Only relevant if you're using the
				   // non-model version

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister) // We got need for
							      // a non-typical
							      // icon currently.
							      // Will be phased
							      // out
    {
	this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowAuto");
	this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowAuto_Empty");
	this.Icon_Unchambered = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowAuto_Unchambered");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) // Onhand display
    {
	if (this.getDamage(stack) >= this.getMaxDamage())
	{
	    return this.Icon_Empty;
	}
	if (!this.getChambered(stack))
	{
	    return this.Icon_Unchambered;
	} // Not chambered

	return this.Icon;
    }*/

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= this.getMaxDamage())
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Is empty

	if (!this.getChambered(stack)) // No arrow on the rail
	{
	    if (player.isSneaking())
	    {
		this.setChambered(stack, world, player, true);
	    } // Setting up a new arrow

	    return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	if (player.isSneaking())
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Still sneaking, even though you have an arrow on the rail? Not
	  // having it

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
	this.setChambered(stack, world, entity, false); // That bolt has left
							// the rail
    }

    private boolean getChambered(ItemStack stack)
    {
	if (stack.getTagCompound() == null)
	{
	    return false;
	} // Doesn't have a tag

	return stack.getTagCompound().getBoolean("isChambered");
    }

    private void setChambered(ItemStack stack, World world, Entity entity, boolean toggle)
    {
	if (stack.getTagCompound() == null)
	{
	    stack.setTagCompound(new NBTTagCompound());
	} // Init

	stack.getTagCompound().setBoolean("isChambered", toggle); // Done, we're
								  // good to go
								  // again

	// SFX
	Utils.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 0.5F);
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
	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 10 ticks)", 10).getInt();

	this.isMobUsable = config.get(this.name,
		"Can I be used by QuiverMobs? (default false. They don't know how to rechamber me.)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One auto-crossbow (empty)
	    GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "iii", "pcp", " t ", 'i',
		    Items.IRON_INGOT, 'p', Blocks.PISTON, 't', Blocks.TRIPWIRE_HOOK, 'c',
		    Helper.getWeaponStackByClass(Crossbow_Double.class, true));
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addShapelessRecipe(new ItemStack(this), // Fill the empty
							     // auto-crossbow
							     // with one arrow
							     // bundle
		Helper.getAmmoStack(ArrowBundle.class, 0), new ItemStack(this, 1, this.getMaxDamage()));
    }

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	if (stack.getItemDamage() >= stack.getMaxDamage())
	{
	    return "CrossbowAuto_empty";
	} // Empty
	if (!this.getChambered(stack))
	{
	    return "CrossbowAuto_unchambered";
	} // Not chambered

	return "CrossbowAuto"; // Regular
    }
}
