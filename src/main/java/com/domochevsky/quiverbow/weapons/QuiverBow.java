package com.domochevsky.quiverbow.weapons;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;
import com.domochevsky.quiverbow.weapons.base.WeaponBow;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class QuiverBow extends WeaponBow
{
    public QuiverBow()
    {
	super("quiverbow", 256);
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft)
    {
	int j = this.getMaxItemUseDuration(stack) - timeLeft; // Reduces the
	// durability by
	// the
	// ItemInUseCount
	// (probably 1 for
	// anything that
	// isn't a tool)

	if (entityLiving instanceof EntityPlayer)
	{
	    ArrowLooseEvent event = new ArrowLooseEvent((EntityPlayer) entityLiving, stack, world, j, false);
	    MinecraftForge.EVENT_BUS.post(event);
	    if (event.isCanceled())
	    {
		return;
	    }
	    j = event.getCharge();
	}

	if (this.getDamage(stack) == stack.getMaxDamage())
	{
	    return;
	} // No arrows in the quiver? Getting out of here early

	float f = j / 20.0F;
	f = (f * f + f * 2.0F) / 3.0F;

	if (f < 0.1D)
	{
	    return;
	}
	if (f > 1.0F)
	{
	    f = 1.0F;
	}

	Helper.playSoundAtEntityPos(entityLiving, SoundEvents.ENTITY_ARROW_SHOOT, 1.0F,
		1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

	if(!world.isRemote)
	{
	    EntityArrow entityarrow = Helper.createArrow(world, entityLiving);
	    entityarrow.setAim(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, f * 3.0F, 1.0F);
	    if (f == 1.0F)
	    {
		entityarrow.setIsCritical(true);
	    }

	    if (entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).capabilities.isCreativeMode)
	    {
		entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
	    }
	    else
	    {
		entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
		stack.setItemDamage(this.getDamage(stack) + 1); // Reversed.
		// MORE Damage
		// for a shorter
		// durability
		// bar
	    }

	    world.spawnEntity(entityarrow);
	}
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
	ItemStack stack = player.getHeldItem(hand);
	ArrowNockEvent event = new ArrowNockEvent(player, stack, hand, world, false);
	MinecraftForge.EVENT_BUS.post(event);
	if (event.isCanceled())
	{
	    return event.getAction();
	}

	// Are there any arrows in the quiver?
	if (this.getDamage(stack) < stack.getMaxDamage())
	{
	    player.setActiveHand(hand);
	}

	return ActionResult.<ItemStack>newResult(EnumActionResult.SUCCESS, stack);
    }
    
    @Override
    public void addProps(FMLPreInitializationEvent event, Configuration config)
    {
	this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

	this.isMobUsable = config.get(this.name,
		"Can I be used by QuiverMobs? (default false. They don't know how to span the string.)", false)
		.getBoolean(true);
    }

    @Override
    public void addRecipes() // Enabled defines whether or not the item can be
    // crafted. Reloading existing weapons is always
    // permitted.
    {
	if (this.Enabled)
	{
	    // One quiverbow with 256 damage value (empty)
	    GameRegistry.addRecipe(Helper.createEmptyWeaponOrAmmoStack(this, 1), "zxy", "xzy", "zxy", 'x', Items.STICK,
		    'y', Items.STRING, 'z', Items.LEATHER);
	}
	else if (Main.noCreative)
	{
	    this.setCreativeTab(null);
	} // Not enabled and not allowed to be in the creative menu

	// Ammo
	ItemStack bundle = Helper.getAmmoStack(ArrowBundle.class, 0);
	GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(bundle.getItem(), 8));
    }
}
