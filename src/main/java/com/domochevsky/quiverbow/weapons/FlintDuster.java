package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.BoxOfFlintDust;
import com.domochevsky.quiverbow.projectiles.FlintDust;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FlintDuster extends WeaponBase
{
	public FlintDuster()
	{
		super("flint_duster", 256);
		this.setCreativeTab(CreativeTabs.TOOLS); // Tool, so on the tool tab
	}

	private int damage;
	private int maxBlocks;

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
		// Ignoring cooldown for firing purposes

		// SFX
		Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_BAT_TAKEOFF, 0.5F, 0.6F);

		if (!world.isRemote)
		{
			// Ready
			FlintDust shot = new FlintDust(world, entity, (float) this.speed);

			// Properties
			shot.damage = this.damage;
			shot.ticksInAirMax = this.maxBlocks;

			// Go
			world.spawnEntity(shot);
		}

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, 4);
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.speed = 1.5f; // Fixed value

		this.damage = config.get(this.name, "What damage am I dealing? (default 1)", 1).getInt();
		this.maxBlocks = config.get(this.name, "How much range do I have? (default ~7 blocks)", 7).getInt();

		this.isMobUsable = config
				.get(this.name, "Can I be used by QuiverMobs? (default false. They have no interest in dirt.)", false)
				.getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.enabled)
		{
			// One Flint Duster (Empty)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "qhq", "qpq", "tsi", 'p',
					Blocks.PISTON, 's', Blocks.STICKY_PISTON, 'h', Blocks.HOPPER, 'q', Blocks.QUARTZ_BLOCK, 'i',
					Items.IRON_INGOT, 't', Blocks.TRIPWIRE_HOOK);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		ItemStack stack = Helper.getAmmoStack(BoxOfFlintDust.class, 0);
		GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(stack.getItem(), 32));
	}
}
