package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.SnowShot;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SnowCannon extends _WeaponBase
{
    public SnowCannon()
    {
	super("snow_cannon", 64);
    }

    private int Slow_Strength; // -15% speed per level. Lvl 3 = -45%
    private int Slow_Duration;

    /*
     * @SideOnly(Side.CLIENT)
     * 
     * @Override public void registerIcons(IIconRegister par1IconRegister) {
     * this.Icon =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/SnowCannon");
     * this.Icon_Empty =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/SnowCannon_Empty");
     * }
     */

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
	if (this.getCooldown(stack) > 0)
	{
	    return;
	} // Hasn't cooled down yet

	Helper.knockUserBack(entity, this.Kickback); // Kickback

	// SFX
	entity.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 0.5F);

	this.setCooldown(stack, this.Cooldown); // Cooling down now

	int counter = 0;

	while (counter < 4) // Scatter 4
	{
	    this.fireShot(world, entity); // Firing!

	    if (this.consumeAmmo(stack, entity, 1))
	    {
		return;
	    }
	    // else, still has ammo left. Continue.

	    counter += 1;
	}
    }

    private void fireShot(World world, Entity entity)
    {
	float spreadHor = world.rand.nextFloat() * 20 - 10; // Spread between -5
							    // and 5
	float spreadVert = world.rand.nextFloat() * 20 - 10;

	SnowShot snow = new SnowShot(world, entity, (float) this.Speed, spreadHor, spreadVert,
		new PotionEffect(MobEffects.SLOWNESS, this.Slow_Duration, this.Slow_Strength));

	// Random Damage
	int dmg_range = this.DmgMax - this.DmgMin; // If max dmg is 20 and min
						   // is 10, then the range will
						   // be 10
	int dmg = world.rand.nextInt(dmg_range + 1); // Range will be between 0
						     // and 10
	dmg += this.DmgMin; // Adding the min dmg of 10 back on top, giving us
			    // the proper damage range (10-20)

	snow.damage = dmg;

	world.spawnEntity(snow);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.DmgMin = config.get(this.name, "What damage am I dealing, at least? (default 1)", 1).getInt();
	this.DmgMax = config.get(this.name, "What damage am I dealing, tops? (default 2)", 2).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5)
		.getDouble();
	this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 2)", 2)
		.getInt();
	this.Cooldown = config.get(this.name, "How long until I can fire again? (default 15 ticks)", 15).getInt();

	this.Slow_Strength = config.get(this.name, "How strong is my Slowness effect? (default 3)", 3).getInt();
	this.Slow_Duration = config.get(this.name, "How long does my Slowness effect last? (default 40 ticks)", 40)
		.getInt();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One redstone sprayer (empty)
	    GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "zxz", "zbz", "aya", 'x', Blocks.PISTON,
		    'y', Blocks.TRIPWIRE_HOOK, 'z', Blocks.WOOL, 'a', Blocks.OBSIDIAN, 'b', Blocks.STICKY_PISTON);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(Blocks.SNOW, 4));
    }

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	if (stack.getItemDamage() >= stack.getMaxDamage())
	{
	    return "SnowCannon_empty";
	}
	if (this.getCooldown(stack) > 0)
	{
	    return "SnowCannon_hot";
	} // Cooling down

	return "SnowCannon"; // Regular
    }
}
