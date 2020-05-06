package com.domochevsky.quiverbow.armsassistant;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import com.domochevsky.quiverbow.miscitems.PackedUpAA;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import daomephsta.umbra.streams.NBTCollectors;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class EntityArmsAssistant extends EntityCreature implements IEntityAdditionalSpawnData, IEntityOwnable, IRangedAttackMob
{
	private UUID ownerUUID;
	private IItemHandlerModifiable inventory = new ItemStackHandler(4);
	private Collection<IArmsAssistantUpgrade> upgrades = new HashSet<>();
	private ArmsAssistantDirectives directives;

	public EntityArmsAssistant(World world)
	{
		super(world);
		this.setSize(1.0F, 1.2F);
		//TODO read from NBT
		this.directives = ArmsAssistantDirectives.defaultDirectives(this);
	}

	public EntityArmsAssistant(World world, EntityPlayer player)
	{
		this(world);
		this.ownerUUID = player.getPersistentID();
		this.directives = ArmsAssistantDirectives.defaultDirectives(this);
	}

	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
	{
        IEntityLivingData livingDataSuper = super.onInitialSpawn(difficulty, livingdata);
        //Apply upgrade attribute modifiers
        Multimap<String, AttributeModifier> modifiers = MultimapBuilder.hashKeys().arrayListValues().build();
        for (IArmsAssistantUpgrade upgrade : upgrades)
            upgrade.submitAttributeModifiers(modifiers::put);
        getAttributeMap().applyAttributeModifiers(modifiers);
        //Set home pos for use by STAY AI, home distance is ignored and thus arbitrary
        setHomePosAndDistance(getPosition(), 8);
        double moveSpeed = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
        tasks.addTask(2, new EntityAIAttackRanged(this, moveSpeed, 20, 16.0F));
		return livingDataSuper;
	}

	@Override
	protected void initEntityAI()
	{
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		int slot = 0;
		for (ItemStack stack : getHeldEquipment())
		{
			stack.updateAnimation(world, this, slot++, true);
		}
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		if (!player.getPersistentID().equals(ownerUUID)) return false;
		ItemStack playerHandStack = player.getHeldItem(hand);
		if (playerHandStack.isEmpty())
		{
			// Drop weapons or fold up
			if (player.isSneaking())
			{
				// Not holding anything, so fold up
				if (getHeldItemMainhand().isEmpty() && getHeldItemOffhand().isEmpty())
				{
					dropSelf();
					setDead();
				}
				else dropEquipment();
				return true;
			}
			else if (upgrades.contains(UpgradeRegistry.RIDING))
			{
				player.startRiding(this);
				return true;
			}

			return false;
		}
		// Equip weapon
		else if (playerHandStack.getItem() instanceof WeaponBase)
		{
			// Take weapon from player
			if (!player.capabilities.isCreativeMode) player.setHeldItem(hand, ItemStack.EMPTY);

			// Put it on the first rail, unless the player is sneaking and the
			// turret has a second rail
			if (player.isSneaking() && upgrades.contains(UpgradeRegistry.EXTRA_WEAPON))
				replaceWeapon(EnumHand.OFF_HAND, playerHandStack);
			else
				replaceWeapon(EnumHand.MAIN_HAND, playerHandStack);

			return true;
		}
		// Repair if damaged
		else if (playerHandStack.getItem() == Item.getItemFromBlock(Blocks.IRON_BLOCK) && getHealth() < getMaxHealth())
		{
			heal(20);
			NetHelper.sendParticleMessage(player, this, EnumParticleTypes.FIREWORKS_SPARK, (byte) 4);
			playSound(SoundEvents.BLOCK_ANVIL_USE, 0.7f, 1.0f);

			if (!player.capabilities.isCreativeMode) playerHandStack.shrink(1);
		}
		else if (playerHandStack.getItem() == Items.NAME_TAG)
		{
			//NO OP to let the name tag do its thing
		}
		// Add to inventory
		else
		{
			ItemStack resultStack = playerHandStack;
			for (int slot = 0; slot < inventory.getSlots(); slot++)
			{
				ItemStack stack = inventory.insertItem(slot, playerHandStack, false);
				if (stack.isEmpty())
				{
					resultStack = stack;
					if ((playerHandStack.getItem() == Items.WRITTEN_BOOK || playerHandStack.getItem() == Items.WRITABLE_BOOK)
					    && !hasCustomDirectives())
			        {
			            try
			            {
			                updateDirectives(ArmsAssistantDirectives.from(this, playerHandStack, error ->
			                {
			                    if (!world.isRemote && getOwner() != null)
			                        getOwner().sendMessage(error);
			                }));
			            }
			            catch (Exception e)
			            {
			                e.printStackTrace();
			            }
			        }
					NetHelper.sendTurretInventoryMessageToPlayersInRange(world, this, stack, slot);
					break;
				}
			}
			if (!player.capabilities.isCreativeMode) player.setHeldItem(hand, resultStack);

			return true;
		}

		return false;
	}

	private void dropSelf()
	{
		if (world.isRemote) return;
		dropEquipment();
		ItemStack selfStack = PackedUpAA.createPackedArmsAssistant(this);
		EntityItem self = new EntityItem(world, posX, posY, posZ, selfStack);
		world.spawnEntity(self);
	}

	private void dropEquipment()
	{
		if (world.isRemote) return;

		// Drop weapons
		for (EnumHand handValue : EnumHand.values())
		{
			ItemStack handStack = getHeldItem(handValue);
			if (!handStack.isEmpty())
			{
				setHeldItem(handValue, ItemStack.EMPTY);
				entityDropItem(handStack, 0.0F);
			}
		}
		// Drop inventory
		for (int slot = 0; slot < inventory.getSlots(); slot++)
		{
			ItemStack stack = inventory.getStackInSlot(slot);
			if (!stack.isEmpty())
			{
				inventory.extractItem(slot, stack.getCount(), false);
				NetHelper.sendTurretInventoryMessageToPlayersInRange(world, this, stack, slot);
				entityDropItem(stack, 0.0F);
			}
		}
		updateDirectives(ArmsAssistantDirectives.defaultDirectives(this));
	}

	private void replaceWeapon(EnumHand hand, ItemStack replacement)
	{
		ItemStack previousWeapon = getHeldItem(hand);
		setHeldItem(hand, replacement.copy());
		if (!world.isRemote)
		{
			EntityItem droppedWeapon = new EntityItem(world, posX, posY, posZ, previousWeapon);
			droppedWeapon.setDefaultPickupDelay();
			world.spawnEntity(droppedWeapon);
		}
	}

	private void updateDirectives(ArmsAssistantDirectives newDirectives)
	{
	    this.directives.revertAI();
	    newDirectives.applyAI();
	    this.directives = newDirectives;
	}

	public boolean hasCustomDirectives()
	{
	    return directives.areCustom();
	}

	@Override
	public void onDeath(DamageSource source)
	{
		super.onDeath(source);
		this.playSound(SoundEvents.BLOCK_METAL_BREAK, 0.8f, 0.3f);

		if (!this.world.isRemote) // Spill it all (server-side)
		{
			// Drop weapons
			for (EnumHand handValue : EnumHand.values())
			{
				ItemStack handStack = getHeldItem(handValue);
				if (!handStack.isEmpty())
				{
					setHeldItem(handValue, ItemStack.EMPTY);
					entityDropItem(getHeldItem(handValue), 0.0F);
				}
			}
			// Drop inventory
			for (int slot = 0; slot < inventory.getSlots(); slot++)
			{
				ItemStack stack = inventory.getStackInSlot(slot);
				if (!stack.isEmpty())
				{
					inventory.extractItem(slot, stack.getCount(), false);
					entityDropItem(stack, 0.0F);
				}
			}
			//TODO Drop parts

			//TODO Inform owner of AA death on death
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
		return super.getCapability(capability, facing);
	}

    public Collection<IArmsAssistantUpgrade> getUpgrades()
    {
        return upgrades;
    }

	public boolean hasUpgrade(IArmsAssistantUpgrade upgrade)
	{
		return upgrades.contains(upgrade);
	}

	public boolean applyUpgrade(IArmsAssistantUpgrade upgrade)
	{
		return upgrades.add(upgrade);
	}

	@Override
	public UUID getOwnerId()
	{
		return ownerUUID;
	}

	@Override
	public Entity getOwner()
	{
		return ownerUUID != null
			? world.getPlayerEntityByUUID(ownerUUID)
			: null;
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		return super.isEntityInvulnerable(source) || source == DamageSource.IN_WALL || source == DamageSource.STARVE;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		boolean result = super.attackEntityFrom(source, amount);
		if (!this.world.isRemote && this.getHealth() < this.getMaxHealth() / 3)
		{
			//TODO Inform owner of low AA health
		}
		return result;
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
	{
		if (!tryFireWeapon(getHeldItemMainhand()))
		    tryFireWeapon(getHeldItemOffhand());
	}

	private boolean tryFireWeapon(ItemStack weaponStack)
	{
	    if (!weaponStack.isEmpty() && weaponStack.getItem() instanceof WeaponBase)
        {
            WeaponBase weapon = (WeaponBase) weaponStack.getItem();
            if (weapon.getCooldown(weaponStack) == 0)
            {
                return weapon.doSingleFire(world, this, weaponStack, EnumHand.MAIN_HAND);
            }
        }
        return false;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
	{
		return SoundEvents.BLOCK_ANVIL_LAND;
	}

	@Override
	public String getName()
	{
		if (this.hasCustomName())
		{
			return this.getCustomNameTag();
		}
		return "ARMS ASSISTANT " + getEntityId();
	}


	@Override
	protected EntityBodyHelper createBodyHelper()
	{
		return new EntityBodyHelper(this)
		{
			@Override
			public void updateRenderAngles() {}
		};
	}

	@Override
	public boolean canBeSteered()
	{
		return upgrades.contains(UpgradeRegistry.RIDING); // Can be steered if we have this upgrade
	}

	@Override
	public double getMountedYOffset()
	{
		return this.height * 0.8;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
	{
		return this.getEntityBoundingBox();
	}

	@Override
	protected boolean canDespawn()
	{
		return false;
	}

	@Override
	public boolean canBreatheUnderwater()
	{
		return true;
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {}

	private static final String TAG_OWNER = "ownerUUID", TAG_INV = "inventory", TAG_UPGRADES = "upgrades";

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		if (ownerUUID != null) compound.setTag(TAG_OWNER, NBTUtil.createUUIDTag(ownerUUID));
		compound.setTag(TAG_INV, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventory, null));
		compound.setTag(TAG_UPGRADES,
			upgrades.stream()
			.map(upgrade -> new NBTTagString(UpgradeRegistry.getUpgradeID(upgrade).toString()))
			.collect(NBTCollectors.toNBTList(NBTTagString.class)));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		if (compound.hasKey(TAG_OWNER)) ownerUUID = NBTUtil.getUUIDFromTag(compound.getCompoundTag(TAG_OWNER));
		CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventory, null, compound.getTag(TAG_INV));
		NBTTagList upgradesTag = compound.getTagList(TAG_UPGRADES, NBT.TAG_STRING);
		for (int t = 0; t < upgradesTag.tagCount(); t++)
		{
			upgrades.add(UpgradeRegistry.getUpgradeInstance(new ResourceLocation(upgradesTag.getStringTagAt(t))));
		}
	}

	private static final int HAS_UUID = 0;

	@Override
	public void writeSpawnData(ByteBuf buffer)
	{
		BitSet flags = new BitSet();
		flags.set(HAS_UUID, ownerUUID != null);
		NetHelper.writeBitSet(buffer, flags);
		buffer.writeInt(upgrades.size());
		for (IArmsAssistantUpgrade upgrade : upgrades)
		{
			buffer.writeInt(UpgradeRegistry.getUpgradeIntegerID(upgrade));
		}
		for (int slot = 0; slot < inventory.getSlots(); slot++)
		{
			ByteBufUtils.writeItemStack(buffer, inventory.getStackInSlot(slot));
		}
		if (ownerUUID != null)
		{
			buffer.writeLong(ownerUUID.getMostSignificantBits());
			buffer.writeLong(ownerUUID.getLeastSignificantBits());
		}
	}

	@Override
	public void readSpawnData(ByteBuf buffer)
	{
		BitSet flags = NetHelper.readBitSet(buffer);
		int upgradeCount = buffer.readInt();
		for (int i = 0; i < upgradeCount; i++)
		{
			upgrades.add(UpgradeRegistry.getUpgradeInstance(buffer.readInt()));
		}
		for (int slot = 0; slot < inventory.getSlots(); slot++)
		{
			inventory.setStackInSlot(slot, ByteBufUtils.readItemStack(buffer));
		}
		if (flags.get(HAS_UUID))
		{
			long uuidMost = buffer.readLong();
			long uuidLeast = buffer.readLong();
			ownerUUID = new UUID(uuidMost, uuidLeast);
		}
	}
}
