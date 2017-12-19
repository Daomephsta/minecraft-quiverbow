package com.domochevsky.quiverbow.weapons;

import java.util.ArrayList;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.HealthBeam;
import com.domochevsky.quiverbow.recipes.Recipe_RayOfHope_Reload;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

public class MediGun extends _WeaponBase
{
	public MediGun()
	{
		super("ray_of_hope", 320);
	}

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
		// Good to go (already verified)

		// SFX
		Helper.playSoundAtEntityPos(entity, SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.4F);

		if (!world.isRemote)
		{
			HealthBeam beam = new HealthBeam(entity.world, entity, (float) this.Speed);

			beam.ignoreFrustumCheck = true;
			beam.ticksInAirMax = 40;

			entity.world.spawnEntity(beam); // Firing!

			this.consumeAmmo(stack, entity, 1);
			this.setCooldown(stack, this.Cooldown);
		}
	}

	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.Speed = config.get(this.name, "How fast are my beams? (default 5.0 BPT (Blocks Per Tick))", 5.0)
				.getDouble();

		this.isMobUsable = config.get(this.name,
				"Can I be used by QuiverMobs? (default false. They don't know what friends are.)", false)
				.getBoolean(true);
	}

	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// Use a beacon for this (+ obsidian, tripwire hook... what else)
			GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "bi ", "ico", " ot", 'b',
					Blocks.BEACON, 'o', Blocks.OBSIDIAN, 't', Blocks.TRIPWIRE_HOOK, 'c', Items.CAULDRON, 'i',
					Items.IRON_INGOT);
		}
		else if (Main.noCreative)
		{
			this.setCreativeTab(null);
		} // Not enabled and not allowed to be in the creative menu

		RecipeSorter.register("quiverchevsky:recipehandler_roh_reload", Recipe_RayOfHope_Reload.class,
				RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

		ArrayList<ItemStack> list = new ArrayList<ItemStack>();

		list.add(new ItemStack(Items.POTIONITEM, 1, 8193));
		list.add(new ItemStack(Items.POTIONITEM, 1, 8225));

		GameRegistry.addRecipe(new Recipe_RayOfHope_Reload(new ItemStack(this), list,
				new ItemStack(Items.POTIONITEM, 1, 8193), new ItemStack(Items.POTIONITEM, 1, 8225)));
	}
}
