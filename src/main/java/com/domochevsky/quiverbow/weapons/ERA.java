package com.domochevsky.quiverbow.weapons;

import java.util.List;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderAccelerator;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.domochevsky.quiverbow.weapons.base.firingbehaviours.FiringBehaviourBase;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
//TODO: JEI integration to direct players to craft the incomplete ERA
public class ERA extends WeaponBase
{
    private static final String PROP_SELF_EXPLOSION_SIZE = "selfExplosionSize";
    private static final String REINFORCED_MUZZLE = "hasEmeraldMuzzle";
	private class ERAFiringBehaviour extends FiringBehaviourBase<ERA>
	{
		protected ERAFiringBehaviour()
		{
			super(ERA.this);
		}

		@Override
		public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
		{
			if (ERA.isAccelerating(stack))
			{
				return;
			} // Already in the middle of firing

			// Firing
			ERA.startAccelerating(stack);
		}

		@Override
		public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
		{}
	}

	public ERA()
	{
		super("ender_rail_accelerator", 1);
		setFiringBehaviour(new ERAFiringBehaviour());
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
	{
		// Used for ticking up 27 * 2 times (with increasing pitch) after
		// triggered and before firing
		// 54 ticks minimum per shot (movement in/out)

		if (ERA.isAccelerating(stack))
		{
			stack.getTagCompound().setInteger("acceleration", stack.getTagCompound().getInteger("acceleration") - 1); // Ticking
			// down
			stack.getTagCompound().setFloat("accSFX", stack.getTagCompound().getFloat("accSFX") + 0.02f); // And
			// pitching
			// up

			Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
					stack.getTagCompound().getFloat("accSFX"), stack.getTagCompound().getFloat("accSFX"));
			// mob.endermen.portal
			// mob.enderdragon.wings

			if (stack.getTagCompound().getInteger("acceleration") <= 0) // Ready
			// to
			// fire
			{
				Helper.knockUserBack(entity, this.getKickback()); // Kickback

				// Upgrade
				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean(REINFORCED_MUZZLE))
				{
					entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 15.0f); // Hurtin'
					// (but
					// less
					// so)
				}
				else
				{
					entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 20.0f); // Hurtin'
				}

				boolean damageTerrain = world.getGameRules().getBoolean("mobGriefing");

				if (!holdingItem) // Isn't holding the weapon, so this is gonna
				// go off in their pockets
				{
					entity.hurtResistantTime = 0; // No rest for the wicked
					world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, getProperties().getFloat(CommonProperties.PROP_EXPLOSION_SIZE),
							getProperties().getBoolean(CommonProperties.PROP_DAMAGE_TERRAIN)); // Big baddaboom

					// Set weapon to "burnt out" (if the user's a player and not
					// in creative mode)
					if (entity instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) entity;

						if (player.capabilities.isCreativeMode)
						{} // Is in creative mode, so not burning out
						else
						{
							stack.setItemDamage(1);
						}
					}
					// else, not a player. Not burning out

					return; // We're done here
				}

				if (entity instanceof EntityPlayer)
				{
					damageTerrain = getProperties().getBoolean(CommonProperties.PROP_DAMAGE_TERRAIN);
				} // Players don't care about mob griefing rules, but play by
					// their own rules

				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean(REINFORCED_MUZZLE))
				{
					// Has a muzzle, so no boom
					Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 2.0F, 0.1F);
					NetHelper.sendParticleMessageToAllPlayers(world, entity,
							EnumParticleTypes.SMOKE_LARGE, (byte) 6);
				}
				else
				{
					world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, getProperties().getFloat(PROP_SELF_EXPLOSION_SIZE),
							damageTerrain); // Hurtin' more
				}

				if (!world.isRemote)
				{
					// Spawn projectile and go
					EnderAccelerator shot = new EnderAccelerator(world, entity, 5.0f);

					// Random Damage
					int dmg_range = getProperties().getDamageMin() - getProperties().getDamageMin(); // If max dmg is 20 and min
														// is
					// 10, then the range will be
					// 10
					int dmg = world.rand.nextInt(dmg_range + 1); // Range will
																	// be
					// between 0 and 10
					dmg += getProperties().getDamageMin(); // Adding the min dmg of 10 back on top,
									// giving
					// us the proper damage range (10-20)

					shot.damage = dmg;
					shot.ticksInAirMax = 120; // 6 sec?
					shot.damageTerrain = damageTerrain;
					shot.explosionSize = getProperties().getFloat(PROP_SELF_EXPLOSION_SIZE);

					world.spawnEntity(shot);
				}

				// Set weapon to "burnt out" (if the user's a player and not in
				// creative mode)
				if (entity instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer) entity;

					if (player.capabilities.isCreativeMode)
					{} // Is in creative mode, so not burning out
					else
					{
						stack.setItemDamage(1);
					}
				}
				// else, not a player. Not burning out
			}
			// else, not ready yet
		}
		// else, all's chill
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
	    super.addInformation(stack, world, list, flags);
	    if (stack.hasTagCompound() && stack.getTagCompound().getBoolean(REINFORCED_MUZZLE))
	        list.add(I18n.format(getUnlocalizedName() + ".reinforced_muzzle"));
	}

	private static void startAccelerating(ItemStack stack)
	{
		if (stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}

		stack.getTagCompound().setInteger("acceleration", 54);
		stack.getTagCompound().setFloat("accSFX", 0.02f);
	}

	private static boolean isAccelerating(ItemStack stack)
	{
		if (stack.getTagCompound() == null)
		{
			return false;
		}

		if (stack.getTagCompound().getInteger("acceleration") <= 0)
		{
			return false;
		} // If this is higher than 0 then it's currently counting down to the
			// moment it fires

		return true; // Seems to check out
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
	    super.getSubItems(tab, subItems);

	    ItemStack loaded = new ItemStack(this, 1, 0);
	    NBTTagCompound loadedNbt = new NBTTagCompound();
	    loadedNbt.setBoolean(REINFORCED_MUZZLE, true);
	    loaded.setTagCompound(loadedNbt);
        subItems.add(loaded);

        ItemStack empty = Helper.createEmptyWeaponOrAmmoStack(this, 1);
        NBTTagCompound emptyNbt = new NBTTagCompound();
        emptyNbt.setBoolean(REINFORCED_MUZZLE, true);
        empty.setTagCompound(emptyNbt);
        subItems.add(empty);
	}

	@Override
	protected WeaponProperties createDefaultProperties()
	{
		return WeaponProperties.builder().minimumDamage(120).maximumDamage(150).kickback(30)
				.floatProperty(PROP_SELF_EXPLOSION_SIZE,
						"How large the explosion at the user location is in blocks. A TNT explosion is 4.0 blocks",
						4.0F)
				.floatProperty(CommonProperties.PROP_EXPLOSION_SIZE, CommonProperties.COMMENT_EXPLOSION_SIZE, 8.0F)
				.booleanProperty(CommonProperties.PROP_DAMAGE_TERRAIN, CommonProperties.COMMENT_DAMAGE_TERRAIN, true)
				.build();
	}
}
