package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.Seed;
import com.domochevsky.quiverbow.util.Utils;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Seedling extends _WeaponBase
{
    public Seedling()
    {
	super("seedling", 32);
    }

    private int Dmg;

    /*
     * @SideOnly(Side.CLIENT)
     * 
     * @Override public void registerIcons(IIconRegister par1IconRegister) {
     * this.Icon =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/Seedling");
     * this.Icon_Empty =
     * par1IconRegister.registerIcon("quiverchevsky:weapons/Seedling_Empty"); }
     */

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	if (this.getDamage(stack) >= stack.getMaxDamage())
	{
	    this.breakWeapon(world, stack, player);
	    return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
	}

	this.doSingleFire(stack, world, player); // Handing it over to the
						 // neutral firing function
	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void doSingleFire(ItemStack stack, World world, Entity entity) // Server
									  // side
    {
	// Good to go (already verified)

	entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.7F);

	float spreadHor = world.rand.nextFloat() * 10 - 5; // Spread
	float spreadVert = world.rand.nextFloat() * 10 - 5;

	Seed shot = new Seed(world, entity, (float) this.Speed, spreadHor, spreadVert);
	shot.damage = this.Dmg;

	world.spawnEntity(shot); // Firing

	if (this.consumeAmmo(stack, entity, 1))
	{
	    this.breakWeapon(world, stack, entity);
	}
    }

    // All ammo has been used up, so breaking now
    private void breakWeapon(World world, ItemStack stack, Entity entity)
    {
	if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
	{
	    this.setCooldown(stack, 40);
	    return;
	}

	EntityPlayer player = (EntityPlayer) entity;

	player.renderBrokenItemStack(stack);
	player.inventory.deleteStack(stack);
	stack.setCount(0);

	EntityItem piston = new EntityItem(world, player.posX, player.posY + 1.0F, player.posZ,
		new ItemStack(Blocks.PISTON));
	piston.setDefaultPickupDelay();

	if (player.captureDrops)
	{
	    player.capturedDrops.add(piston);
	}
	else
	{
	    world.spawnEntity(piston);
	}

	EntityItem hook = new EntityItem(world, player.posX, player.posY + 1.0F, player.posZ,
		new ItemStack(Blocks.TRIPWIRE_HOOK));
	hook.setDefaultPickupDelay();

	if (player.captureDrops)
	{
	    player.capturedDrops.add(hook);
	}
	else
	{
	    world.spawnEntity(hook);
	}

	Utils.playSoundAtEntityPos(player, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.5F);
    }

    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.Dmg = config.get(this.name, "What damage am I dealing per projectile? (default 1)", 1).getInt();

	this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.3 BPT (Blocks Per Tick))", 1.3)
		.getDouble();

	this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
    }

    @Override
    public void addRecipes()
    {
	if (this.Enabled)
	{
	    // One Seedling (fully loaded, meaning 0 damage)
	    GameRegistry.addRecipe(new ItemStack(this, 1, 0), "ada", "ada", "bca", 'a', Items.REEDS, 'b',
		    Blocks.TRIPWIRE_HOOK, 'c', Blocks.PISTON, 'd', Blocks.MELON_BLOCK);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu
    }

    @Override
    public String getModelTexPath(ItemStack stack) // The model texture path
    {
	return "Seedling"; // Regular
    }
}
