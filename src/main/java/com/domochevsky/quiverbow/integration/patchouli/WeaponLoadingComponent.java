package com.domochevsky.quiverbow.integration.patchouli;

import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.ammo.AmmoMagazine;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ComponentData;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ReloadSpecification;
import com.domochevsky.quiverbow.weapons.base.Weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;

public class WeaponLoadingComponent implements ICustomComponent
{
    @VariableHolder
    public String specification;
    private transient ItemStack output;
    private transient ReloadSpecification specificationObj;
    private transient int componentX, componentY;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        this.componentX = componentX;
        this.componentY = componentY;
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(specification));
        if (item instanceof Weapon)
            this.specificationObj = ReloadSpecificationRegistry.INSTANCE.getSpecification((Weapon) item);
        else if (item instanceof AmmoMagazine)
            this.specificationObj = ReloadSpecificationRegistry.INSTANCE.getSpecification((AmmoMagazine) item);
        else
            throw new IllegalArgumentException("Unknown weapon or magazine" + new ResourceLocation(specification));
        this.output = new ItemStack(item);
    }

    @Override
    public void render(IComponentRenderContext context, float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.translate(componentX, componentY, 0);
        int x = 0, y = 0;

        // Inputs
        for (ComponentData component : specificationObj.getComponents())
        {
            renderIOFrame(context, x, y);
            context.renderIngredient(x + 4, y + 4, mouseX, mouseY, component.getIngredient());
            if (component.getMin() == component.getMax())
            {
                context.getFont().drawString(I18n.format("quiverbow_restrung.jei.ammo_loading.quantity",
                    component.getMin()), x + 34, y + 8, 0x000000);
            }
            else
            {
                context.getFont().drawString(I18n.format("quiverbow_restrung.jei.ammo_loading.quantity_range",
                    component.getMin(), component.getMax()), x + 25, y + 8, 0x000000);
            }
            y += 25;
        }

        int outputY = y / 2 - 12;
        // Arrow & Type Icon
        GlStateManager.color(1, 1, 1, 1); // Revert colour change from text renderer
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(QuiverbowMain.MODID, "textures/gui/jei/ammo_loading.png"));
        Gui.drawModalRectWithCustomSizedTexture(52, outputY + 4, 116, 26, 29, 15, 256, 256); // Arrow
        boolean isWeapon = output.getItem() instanceof Weapon;
        Gui.drawModalRectWithCustomSizedTexture(56, outputY + 5, 116, isWeapon ? 0 : 13, 23, 13, 256, 256); // Type Icon
        // Output
        renderIOFrame(context, x + 82, outputY);
        context.renderItemStack(x + 82 + 4, outputY + 4, mouseX, mouseY, output);
        // Type Icon Tooltip
        if (context.isAreaHovered(mouseX, mouseY, componentX + 56, componentY + outputY + 5, 13, 13))
        {
            String translationKey = QuiverbowMain.MODID + (isWeapon
                ? ".jei.ammo_loading.craft"
                : ".jei.ammo_loading.use_magazine");
            int tooltipX = 84, tooltipY = 31;
            GlStateManager.translate(-tooltipX, -tooltipY, 0);
            context.getGui().drawHoveringText(I18n.format(translationKey), mouseX, mouseY);
            GlStateManager.color(1, 1, 1, 1); // Revert colour change from text renderer
            GlStateManager.translate(tooltipX, tooltipY, 0);
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderIOFrame(IComponentRenderContext context, int x, int y)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(context.getCraftingTexture());
        Gui.drawModalRectWithCustomSizedTexture(x, y, 83, 71, 24, 24, 128, 128);
    }
}
