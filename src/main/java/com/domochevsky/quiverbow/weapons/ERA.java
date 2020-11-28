package com.domochevsky.quiverbow.weapons;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

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
    private static final String ACC_SFX = "accSFX";
    private static final String VELOCITY = "acceleration";
    private static final int MAX_VELOCITY = 54;
    public static final Pair<String, String> SELF_EXPLOSION_SIZE = Pair.of("selfExplosionSize",
        "How large the explosion at the user location is in blocks. A TNT explosion is 4.0 blocks");
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
			    return;
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
			stack.getTagCompound().setInteger(VELOCITY, stack.getTagCompound().getInteger(VELOCITY) - 1); // Ticking down
			stack.getTagCompound().setFloat(ACC_SFX, stack.getTagCompound().getFloat(ACC_SFX) + 0.02f); // And pitching up

			Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
					stack.getTagCompound().getFloat(ACC_SFX), stack.getTagCompound().getFloat(ACC_SFX));

			if (stack.getTagCompound().getInteger(VELOCITY) <= 0) // Ready to fire
			{
				Helper.knockUserBack(entity, this.getKickback()); // Kickback Upgrade
				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean(REINFORCED_MUZZLE))
                    entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 15.0f); // Hurtin' (but less so)
                else
                    entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 20.0f); // Hurtin'

				boolean damageTerrain = world.getGameRules().getBoolean("mobGriefing");

				if (!holdingItem) // Isn't holding the weapon, so this is gonna go off in their pockets
				{
					entity.hurtResistantTime = 0; // No rest for the wicked
					world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, getProperties().getFloat(CommonProperties.EXPLOSION_SIZE),
							getProperties().getBoolean(CommonProperties.DAMAGE_TERRAIN)); // Big baddaboom

					// Set weapon to "burnt out" (if the user's a player and not in creative mode)
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
					damageTerrain = getProperties().getBoolean(CommonProperties.DAMAGE_TERRAIN);
				} // Players don't care about mob griefing rules, but play by their own rules

				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean(REINFORCED_MUZZLE))
				{
					// Has a muzzle, so no boom
					Helper.playSoundAtEntityPos(entity, SoundEvents.ENTITY_GENERIC_EXPLODE, 2.0F, 0.1F);
					NetHelper.sendParticleMessageToAllPlayers(world, entity,
							EnumParticleTypes.SMOKE_LARGE, (byte) 6);
				}
				else
				{
					world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, getProperties().getFloat(SELF_EXPLOSION_SIZE),
							damageTerrain); // Hurtin' more
				}

				// Spawn projectile and go
				world.spawnEntity(new EnderAccelerator(world, entity, getProperties()));

				// Set weapon to "burnt out" (if the user's a player and not in creative mode)
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
		if (!stack.hasTagCompound())
		    stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setInteger(VELOCITY, MAX_VELOCITY);
		stack.getTagCompound().setFloat(ACC_SFX, 0.02f);
	}

	private static boolean isAccelerating(ItemStack stack)
	{
	    return stack.hasTagCompound() && stack.getTagCompound().getInteger(VELOCITY) > 0;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
	    if (!isInCreativeTab(tab)) return;
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
		return WeaponProperties.builder().minimumDamage(120).maximumDamage(150).kickback(30).projectileSpeed(5.0F)
				.floatProperty(SELF_EXPLOSION_SIZE,4.0F)
				.floatProperty(CommonProperties.EXPLOSION_SIZE, 8.0F)
				.booleanProperty(CommonProperties.DAMAGE_TERRAIN, true)
				.build();
	}
}
