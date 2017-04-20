package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.net.NetHelper;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PowderKnuckle extends _WeaponBase
{
	public PowderKnuckle() { super("powder_knuckles", 8); }

	

	private double ExplosionSize;

	private boolean dmgTerrain;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/PowderKnuckle");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/PowderKnuckle_Empty");
	}


	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sideX, float sideY, float sideZ)
	{
		if (world.isRemote) { return false; }	// Not doing this on client side

		// Right click
		if (this.getDamage(stack) >= this.getMaxDamage()) { return false; }	// Not loaded

		if (!player.capabilities.isCreativeMode) { this.consumeAmmo(stack, player, 1); }

		world.createExplosion(player, x, y, z, (float) this.ExplosionSize, true);

		NetHelper.sendParticleMessageToAllPlayers(world, player.getEntityId(), (byte) 3, (byte) 4);	// smoke

		return true;
	}


	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		if (player.worldObj.isRemote) { return false; }	// Not doing this on client side

		if (this.getDamage(stack) >= this.getMaxDamage())
		{
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.DmgMin);
			entity.hurtResistantTime = 0;	// No invincibility frames

			return false; 				// We're not loaded, getting out of here with minimal damage
		}

		this.consumeAmmo(stack, entity, 1);

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(entity.worldObj, player.getEntityId(), (byte) 3, (byte) 4);	// smoke

		// Dmg
		entity.setFire(2);																	// Setting fire to them for 2 sec, so pigs can drop cooked porkchops
		entity.worldObj.createExplosion(player, entity.posX, entity.posY + 0.5D, entity.posZ, (float) this.ExplosionSize, this.dmgTerrain); 	// 4.0F is TNT

		entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.DmgMax);	// Dealing damage directly. Screw weapon attributes

		return false;
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.DmgMin = config.get(this.name, "What's my minimum damage, when I'm empty? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.name, "What's my maximum damage when I explode? (default 18)", 18).getInt();

		this.ExplosionSize = config.get(this.name, "How big are my explosions? (default 1.5 blocks. TNT is 4.0 blocks)", 1.5).getDouble();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true).getBoolean(true);

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false. They don't know where the trigger on this thing is.)", false).getBoolean(false);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Powder Knuckle with 8 damage value (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "yyy", "xzx", "x x",
					'x', Items.leather,
					'y', Items.iron_ingot,
					'z', Items.stick
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack stack = new ItemStack(Items.gunpowder);

		Helper.makeAmmoRecipe(stack, 1, 1, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 2, 2, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 3, 3, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 4, 4, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 5, 5, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 6, 6, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 7, 7, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 8, 8, this.getMaxDamage(), this);
	}
}
