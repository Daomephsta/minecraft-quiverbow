package com.domochevsky.quiverbow.ammo;

import com.domochevsky.quiverbow.util.InventoryHelper;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class LapisMagazine extends AmmoMagazine
{
    public LapisMagazine()
    {
	super();
	this.setMaxDamage(150);		// Filled with lapis
	this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons = new IIcon[6];

    @Override
    public String getIconPath()
    {
	return "LapisAmmo";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister) 
    { 
	this.iconEmpty = par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_0");
	for(int i = 0; i < 6; i++)
	{
	    this.icons[i] = par1IconRegister.registerIcon("quiverchevsky:ammo/LapisAmmo_" + (i + 1));
	}
    }


    @Override
    public IIcon getIconFromDamage(int meta) 
    {
	if (meta == this.getMaxDamage()) return this.iconEmpty;
	else return icons[(int) (5 - Math.floor(meta / 25.0F))];
    }


    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) 
    {  			// Not doing this on client side
	if (stack.getItemDamage() == 0) { return stack; }	// Already fully loaded
	if (stack.getItemDamage() < 25) { return stack; }	// No room for another lapis block

	if(player.capabilities.isCreativeMode)
	{
	    if(world.isRemote) Minecraft.getMinecraft().ingameGUI.func_110326_a(I18n.format("quiverchevsky.ammo.nocreative"), false);	
	    return stack;
	}
	if (hasComponentItems(player, 1))
	{
	    //this.consumeItemStack(player.inventory, this.lapisStack);	// We're just grabbing what we need from the inventory

	    int dmg = stack.getItemDamage() - 25;
	    stack.setItemDamage(dmg);

	    consumeComponentItems(player, 1);
	}

	return stack;
    }


    @Override
    public void addRecipes() 
    {
	GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xgx",
		'x', Blocks.glass_pane, 
		'g', new ItemStack(Items.dye, 1, 4)
		);
    }

    @Override
    protected boolean hasComponentItems(EntityPlayer player, int amount)
    {
	return InventoryHelper.hasBlock(player, Blocks.lapis_block, amount);
    }


    @Override
    protected boolean consumeComponentItems(EntityPlayer player, int amount)
    {
	player.getEntityWorld().playSoundAtEntity(player, "random.wood_click", 1.0F, 0.2F);
	return InventoryHelper.consumeBlock(player, Blocks.lapis_block, amount);
    }
}
