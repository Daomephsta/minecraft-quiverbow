package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.Main.Constants;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.models.ISpecialRender;
import com.domochevsky.quiverbow.projectiles.RegularArrow;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Crossbow_Auto extends WeaponCrossbow implements ISpecialRender
{    
    public Crossbow_Auto()
    {
	super("auto_crossbow", 8);
	setFiringBehaviour(new SingleShotFiringBehaviour<Crossbow_Auto>(this, (world, weaponStack, entity, data) ->
	{
	    Crossbow_Auto weapon = (Crossbow_Auto) weaponStack.getItem();
	    RegularArrow entityarrow = new RegularArrow(world, entity, (float) this.Speed);

	    // Random Damage
	    int dmg_range = weapon.DmgMax - weapon.DmgMin; // If max dmg is 20 and min
	    // is 10, then the range will
	    // be 10
	    int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
	    // and 10
	    dmg += weapon.DmgMin; // Adding the min dmg of 10 back on top, giving us
	    // the proper damage range (10-20)

	    entityarrow.damage = dmg;
	    entityarrow.knockbackStrength = weapon.Knockback; // Comes with an inbuild
	    // knockback II

	    return entityarrow;
	})
	{
	    @Override
	    public void fire(ItemStack stack, World world, Entity entity)
	    {
		super.fire(stack, world, entity);
		Crossbow_Auto.setChambered(stack, world, entity, false);
	    }
	});
    }

    @Override
    public void registerRender()
    {
	final ModelResourceLocation empty = new ModelResourceLocation(new ResourceLocation(Constants.MODID, "weapons/" + getRegistryName().getResourcePath() + "_empty"), "inventory");
	final ModelResourceLocation unchambered = new ModelResourceLocation(new ResourceLocation(Constants.MODID, "weapons/" + getRegistryName().getResourcePath() + "_unchambered"), "inventory");
	final ModelResourceLocation chambered = new ModelResourceLocation(new ResourceLocation(Constants.MODID, "weapons/" + getRegistryName().getResourcePath()), "inventory");
	ModelLoader.registerItemVariants(this, empty, unchambered, chambered);
	ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition()
	{
	    @Override
	    public ModelResourceLocation getModelLocation(ItemStack stack)
	    {
		if (stack.getItemDamage() >= stack.getMaxDamage()) return empty;
		if (!Crossbow_Auto.getChambered(stack)) return unchambered;
		return chambered; 
	    }
	});
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= stack.getMaxDamage())
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Is empty

	if (!Crossbow_Auto.getChambered(stack)) // No arrow on the rail
	{
	    if (player.isSneaking())
	    {
		Crossbow_Auto.setChambered(stack, world, player, true);
	    } // Setting up a new arrow

	    return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
	}

	if (player.isSneaking())
	{
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	} // Still sneaking, even though you have an arrow on the rail? Not
	// having it

	firingBehaviour.fire(stack, world, player);
	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }

    private static boolean getChambered(ItemStack stack)
    {
	if (stack.getTagCompound() == null)
	{
	    return false;
	} // Doesn't have a tag

	return stack.getTagCompound().getBoolean("isChambered");
    }

    private static void setChambered(ItemStack stack, World world, Entity entity, boolean toggle)
    {
	if (stack.getTagCompound() == null)
	{
	    stack.setTagCompound(new NBTTagCompound());
	} // Init

	stack.getTagCompound().setBoolean("isChambered", toggle); // Done, we're
	// good to go
	// again

	// SFX
	Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.8F, 0.5F);
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
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "iii", "pcp", " t ", 'i',
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
		Helper.getAmmoStack(ArrowBundle.class, 0), Helper.createEmptyWeaponOrAmmoStack(this, 1));
    }
}
