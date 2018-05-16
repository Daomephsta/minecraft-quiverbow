package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.models.ISpecialRender;
import com.domochevsky.quiverbow.weapons.base.WeaponCrossbow;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CrossbowAuto extends WeaponCrossbow implements ISpecialRender
{
	public CrossbowAuto()
	{
		super("auto_crossbow", 8);
		setFiringBehaviour(new SingleShotFiringBehaviour<CrossbowAuto>(this, (world, weaponStack, entity, data) ->
		{
			CrossbowAuto weapon = (CrossbowAuto) weaponStack.getItem();
			EntityArrow entityarrow = Helper.createArrow(world, entity);

			// Random Damage
			int dmg_range = weapon.damageMax - weapon.damageMin; // If max dmg is 20
															// and min
			// is 10, then the range will
			// be 10
			int dmg = world.rand.nextInt(dmg_range + 1); // Range will be
															// between 0
			// and 10
			dmg += weapon.damageMin; // Adding the min dmg of 10 back on top,
									// giving us
			// the proper damage range (10-20)

			entityarrow.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0.0F, (float)weapon.speed, 0.5F);
			entityarrow.setDamage(dmg);
			entityarrow.setKnockbackStrength(weapon.knockback);

			return entityarrow;
		})
		{
			@Override
			public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
			{
				super.fire(stack, world, entity, hand);
				CrossbowAuto.setChambered(stack, world, entity, false);
			}
		});
	}

	@Override
	public void registerRender()
	{
		final ModelResourceLocation empty = new ModelResourceLocation(
				new ResourceLocation(QuiverbowMain.MODID, "weapons/" + getRegistryName().getResourcePath() + "_empty"),
				"inventory");
		final ModelResourceLocation unchambered = new ModelResourceLocation(new ResourceLocation(QuiverbowMain.MODID,
				"weapons/" + getRegistryName().getResourcePath() + "_unchambered"), "inventory");
		final ModelResourceLocation chambered = new ModelResourceLocation(
				new ResourceLocation(QuiverbowMain.MODID, "weapons/" + getRegistryName().getResourcePath()), "inventory");
		ModelLoader.registerItemVariants(this, empty, unchambered, chambered);
		ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				if (stack.getItemDamage() >= stack.getMaxDamage()) return empty;
				if (!CrossbowAuto.getChambered(stack)) return unchambered;
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

		if (!CrossbowAuto.getChambered(stack)) // No arrow on the rail
		{
			if (player.isSneaking())
			{
				CrossbowAuto.setChambered(stack, world, player, true);
			} // Setting up a new arrow

			return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
		}

		if (player.isSneaking())
		{
			return ActionResult.<ItemStack>newResult(EnumActionResult.FAIL, stack);
		} // Still sneaking, even though you have an arrow on the rail? Not
			// having it

		firingBehaviour.fire(stack, world, player, hand);
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
		this.enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.damageMin = config.get(this.name, "What damage am I dealing, at least? (default 10)", 10).getInt();
		this.damageMax = config.get(this.name, "What damage am I dealing, tops? (default 16)", 16).getInt();

		this.speed = config.get(this.name, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5)
				.getDouble();
		this.knockback = config.get(this.name, "How hard do I knock the target back when firing? (default 1)", 1)
				.getInt();
		this.cooldown = config.get(this.name, "How long until I can fire again? (default 10 ticks)", 10).getInt();

		this.isMobUsable = config.get(this.name,
				"Can I be used by QuiverMobs? (default false. They don't know how to rechamber me.)", false)
				.getBoolean(true);
	}
}
