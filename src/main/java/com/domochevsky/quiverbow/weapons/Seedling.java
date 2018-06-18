package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.projectiles.Seed;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class Seedling extends WeaponBase
{
	public Seedling()
	{
		super("seedling", 32);
		setFiringBehaviour(new SingleShotFiringBehaviour<Seedling>(this, (world, weaponStack, entity, data, properties) ->
		{
			float spreadHor = world.rand.nextFloat() * 10 - 5; // Spread
			float spreadVert = world.rand.nextFloat() * 10 - 5;

			Seed shot = new Seed(world, entity, properties.getProjectileSpeed(), spreadHor, spreadVert);
			shot.damage = Helper.randomIntInRange(world.rand, properties.getDamageMin(), properties.getDamageMax());

			return shot;
		})
		{
			@Override
			public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
			{
				super.fire(stack, world, entity, hand);
				if (stack.getItemDamage() >= stack.getMaxDamage()) weapon.breakWeapon(world, stack, entity);
			}
		});
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

		if (!world.isRemote)
		{
			entity.entityDropItem(new ItemStack(Blocks.PISTON), 1.0F);
			entity.entityDropItem(new ItemStack(Blocks.TRIPWIRE_HOOK), 1.0F);
		}

		Helper.playSoundAtEntityPos(player, SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.5F);
	}

	@Override
	public void doFireFX(World world, Entity entity)
	{
		entity.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 0.6F, 0.7F);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(1).maximumDamage(1).projectileSpeed(1.3F).mobUsable().build();
	}
}
