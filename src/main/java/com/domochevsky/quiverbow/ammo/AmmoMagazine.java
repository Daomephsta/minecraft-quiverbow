package com.domochevsky.quiverbow.ammo;

import java.util.List;

import com.domochevsky.quiverbow.Main.Constants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class AmmoMagazine extends _AmmoBase
{	
    @SideOnly(Side.CLIENT)
    private IIcon iconEmpty;
    //How much should this magazine attempt to fill when sneak-clicked?
    private int sneakFillQuantity;
    //How much should this magazine attempt to fill when not sneak-clicked?
    private int standardFillQuantity;

    public AmmoMagazine()
    {
	this(1, 1);
    }
    
    public AmmoMagazine(int standardFillQuantity, int sneakFillQuantity)
    {
	this.sneakFillQuantity = sneakFillQuantity;
	this.standardFillQuantity = standardFillQuantity;
	
	this.setMaxStackSize(1);
	this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister) 
    { 
	Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/" + getIconPath());
	iconEmpty = par1IconRegister.registerIcon("quiverchevsky:ammo/" + getIconPath() + "_Empty");
    }

    @Override
    abstract String getIconPath();

    @Override
    public IIcon getIconFromDamage(int meta) 
    {
	if (meta == this.getMaxDamage()) { return iconEmpty; }

	return Icon;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) 
    {  
	//if (world.isRemote) { return stack; }				// Not doing this on client side
	if (stack.getItemDamage() == 0) { return stack; }	// Already fully loaded or player is in Creative mode
	if(player.capabilities.isCreativeMode)
	{
	    if(world.isRemote) Minecraft.getMinecraft().ingameGUI.func_110326_a(I18n.format("quiverchevsky.ammo.nocreative"), false);	
	    return stack;
	}
	
	if (player.isSneaking()) 
	    this.fill(stack, world, player, sneakFillQuantity);
	else 
	    this.fill(stack, world, player, standardFillQuantity);

	return stack;
    }

    protected void fill(ItemStack stack, World world, EntityPlayer player, int amount)
    {
	if(!hasComponentItems(player, amount))
	{
	    if(world.isRemote) Minecraft.getMinecraft().ingameGUI.func_110326_a(I18n.format("quiverchevsky.ammo.missingitems"), false);
	    return;
	}
	if(consumeComponentItems(player, amount))
	    stack.setItemDamage(stack.getItemDamage() - amount);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) 
    {
	list.add(EnumChatFormatting.BLUE + I18n.format(Constants.MODID + ".ammo." + this.getIconPath() + ".clipstatus", this.getMaxDamage() - stack.getItemDamage(), this.getMaxDamage()));
	list.add(EnumChatFormatting.YELLOW + I18n.format(Constants.MODID + ".ammo." + this.getIconPath() + ".filltext"));
	list.add(I18n.format(Constants.MODID + ".ammo." + this.getIconPath() + ".description"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) 	// getSubItems
    {
	list.add(new ItemStack(item, 1, 0));
	list.add(new ItemStack( item, 1, this.getMaxDamage() ));
    }
    
    @Override
    public boolean showDurabilityBar(ItemStack stack) { return true; }

    //Does the player have all the items required to refill the magazine? 
    protected abstract boolean hasComponentItems(EntityPlayer player, int amount);
    
    //Consume the items required to refill the magazine.
    protected abstract boolean consumeComponentItems(EntityPlayer player, int amount);
}
