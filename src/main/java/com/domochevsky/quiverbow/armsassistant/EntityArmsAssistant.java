package com.domochevsky.quiverbow.armsassistant;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ComponentData;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ReloadSpecification;
import com.domochevsky.quiverbow.loot.LootHandler;
import com.domochevsky.quiverbow.miscitems.PackedUpAA;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import daomephsta.umbra.streams.NBTCollectors;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
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
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
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
    public static final ResourceLocation LOOT_TABLE_ID = new ResourceLocation(QuiverbowMain.MODID, "entities/arms_assistant");
    private static final DataParameter<Boolean> HAS_CUSTOM_DIRECTIVES = EntityDataManager.createKey(EntityArmsAssistant.class, DataSerializers.BOOLEAN);
	private UUID ownerUUID;
	private IItemHandlerModifiable inventory = new ItemStackHandler(4);
	private Collection<IArmsAssistantUpgrade> upgrades = new HashSet<>();
	private ArmsAssistantDirectives directives;
	@SuppressWarnings("unused") //Will be useful later
    private ItemStack directivesBook;

	public EntityArmsAssistant(World world)
	{
		super(world);
		this.setSize(1.0F, 1.2F);
        updateDirectives(ItemStack.EMPTY);
	}

	public EntityArmsAssistant(World world, EntityPlayer player)
	{
		this(world);
		this.ownerUUID = player.getPersistentID();
        updateDirectives(ItemStack.EMPTY);
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
		return livingDataSuper;
	}

	@Override
	protected void initEntityAI()
	{
	    targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	    double moveSpeed = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
	    tasks.addTask(2, new EntityAIAttackRanged(this, moveSpeed, 20, 16.0F));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	}

	@Override
	protected void entityInit()
	{
	    super.entityInit();
	    this.dataManager.register(HAS_CUSTOM_DIRECTIVES, false);
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
				ItemStack insertionRemainder = inventory.insertItem(slot, playerHandStack, false);
				if (insertionRemainder.isEmpty())
				{
					resultStack = insertionRemainder;
					if ((playerHandStack.getItem() == Items.WRITTEN_BOOK || playerHandStack.getItem() == Items.WRITABLE_BOOK)
					    && !hasCustomDirectives())
			        {
					    updateDirectives(playerHandStack);
			        }
					NetHelper.sendTurretInventoryMessageToPlayersInRange(world, this, insertionRemainder, slot);
					break;
				}
			}
			player.setHeldItem(hand, player.capabilities.isCreativeMode ? playerHandStack.copy() : resultStack);

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
		updateDirectives(ItemStack.EMPTY);
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

	private void updateDirectives(ItemStack directivesBook)
	{
	    if (world.isRemote)
	        return;
	    this.directivesBook = directivesBook;
	    if (this.directives != null)
	        this.directives.revertAI();
	    ArmsAssistantDirectives newDirectives = directivesBook.isEmpty()
	    ? ArmsAssistantDirectives.defaultDirectives(this)
	    : ArmsAssistantDirectives.from(this, directivesBook, error ->
        {
            if (!world.isRemote && getOwner() != null)
                getOwner().sendMessage(error);
        });
	    newDirectives.applyAI();
	    this.directives = newDirectives;
	    getDataManager().set(HAS_CUSTOM_DIRECTIVES, directives.areCustom());
	}

	public boolean hasCustomDirectives()
	{
	    return getDataManager().get(HAS_CUSTOM_DIRECTIVES);
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
			directives.onDeath(source);
		}
	}

	@Override
	protected ResourceLocation getLootTable()
	{
	    return LootHandler.ARMS_ASSISTANT_TABLE;
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
	protected void damageEntity(DamageSource source, float amount)
	{
	    super.damageEntity(source, amount);
	    directives.onDamage(source, amount);
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
	{
		tryFire();
	}

	public void tryFire()
	{
	    if (directives.shouldStaggerFire())
	    {
	        float mainCooldown = tryFireWeapon(EnumHand.MAIN_HAND);
            if (mainCooldown >= 0.5F)
	            tryFireWeapon(EnumHand.OFF_HAND);
	    }
	    else //Simultaneous fire
	    {
	        tryFireWeapon(EnumHand.MAIN_HAND);
	        tryFireWeapon(EnumHand.OFF_HAND);
	    }
	}

	private float tryFireWeapon(EnumHand hand)
	{
	    ItemStack weaponStack = getHeldItem(hand);
	    if (!weaponStack.isEmpty() && weaponStack.getItem() instanceof WeaponBase)
        {
            WeaponBase weapon = (WeaponBase) weaponStack.getItem();
            if (weaponStack.getItemDamage() == weaponStack.getMaxDamage())
                tryReload(weaponStack, weapon);
            int cooldown = weapon.getCooldown(weaponStack);
            if (cooldown == 0 && weapon.doSingleFire(world, this, weaponStack, EnumHand.MAIN_HAND))
            {
                return 0.0F;
            }
            return (float) cooldown / weapon.getMaxCooldown();
        }
        return 1.0F;
	}

    private void tryReload(ItemStack weaponStack, WeaponBase weapon)
    {
        ReloadSpecification specification = ReloadSpecificationRegistry.INSTANCE.getSpecification(weapon);
        if (specification == null) return;
        int ammoValue = 0;
        Object2IntMap<ItemStack> toConsume = new Object2IntArrayMap<>();
        for (ComponentData component : specification.getComponents())
        {
            int componentCount = 0;
            for (int s = 0; s < inventory.getSlots(); s++)
            {
                ItemStack stack = inventory.getStackInSlot(s);
                if (component.getIngredient().apply(stack))
                {
                    componentCount = Math.min(stack.getCount() - componentCount, component.getMax());
                    ammoValue += componentCount * component.getAmmoValue(stack);
                    toConsume.put(stack, componentCount);
                }
            }
            if (componentCount < component.getMin() || componentCount == 0)
                return;
        }
        for (Object2IntMap.Entry<ItemStack> entry : toConsume.object2IntEntrySet())
        {
            ItemStack stack = entry.getKey();
            int componentCount = entry.getIntValue();
            if (stack.getItem().hasContainerItem(stack))
            {
                ItemStack container = stack.getItem().getContainerItem(stack);
                container.setCount(componentCount);
                entityDropItem(container, 0.0F);
            }
            stack.shrink(componentCount);
        }
        weaponStack.setItemDamage(weaponStack.getItemDamage() - ammoValue);
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
		//Find directives book
		for (int s = 0; s < inventory.getSlots(); s++)
		{
		    ItemStack stack = inventory.getStackInSlot(s);
		    if (stack.getItem() == Items.WRITTEN_BOOK || stack.getItem() == Items.WRITABLE_BOOK)
		    {
		        updateDirectives(stack);
		        break;
		    }
		}
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
