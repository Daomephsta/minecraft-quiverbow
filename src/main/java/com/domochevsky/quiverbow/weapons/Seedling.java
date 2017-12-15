package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.Seed;
import com.domochevsky.quiverbow.weapons.base._WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.SingleShotFiringBehaviour;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Seedling extends _WeaponBase
{    
	private int Dmg;

	public Seedling()
	{
		super("seedling", 32);
		setFiringBehaviour(new SingleShotFiringBehaviour<Seedling>(this, (world, weaponStack, entity, data) ->
		{
			float spreadHor = world.rand.nextFloat() * 10 - 5; // Spread
			float spreadVert = world.rand.nextFloat() * 10 - 5;

			Seed shot = new Seed(world, entity, (float) this.Speed, spreadHor, spreadVert);
			shot.damage = this.Dmg;

			return shot;
		})
		{
			@Override
			public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand) 
			{ 
				super.fire(stack, world, entity, hand);
				if(stack.getItemDamage() >= stack.getMaxDamage()) weapon.breakWeapon(world, stack, entity); 
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

		if(!world.isRemote)
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
}
