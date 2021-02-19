package com.domochevsky.quiverbow.integration.jei;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.domochevsky.quiverbow.AmmoContainer;
import com.domochevsky.quiverbow.QuiverbowMain;
import com.domochevsky.quiverbow.ammo.AmmoMagazine;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ComponentData;
import com.domochevsky.quiverbow.ammo.ReloadSpecificationRegistry.ReloadSpecification;
import com.domochevsky.quiverbow.items.ItemRegistry;
import com.domochevsky.quiverbow.weapons.base.Weapon;
import com.google.common.collect.Iterables;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RecipeLoadAmmoCategory implements IRecipeCategory<RecipeLoadAmmoCategory.Wrapper>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(QuiverbowMain.MODID, "textures/gui/jei/ammo_loading.png");
    private static final int WIDTH = 116;
    private static final int HEIGHT = 54;
    public static final String ID = QuiverbowMain.MODID + ".ammo_loading";
    private static final String LANG_PREFIX = QuiverbowMain.MODID + ".jei.ammo_loading";
    private final String translatedTitle;
    private final IDrawable background, tabIcon;

    public RecipeLoadAmmoCategory(IGuiHelper guiHelper)
    {
        this.translatedTitle = I18n.format(LANG_PREFIX + ".title");
        this.background = guiHelper.createDrawable(TEXTURE,
            0, 0, WIDTH, HEIGHT);
        this.tabIcon = guiHelper.createDrawableIngredient(new ItemStack(ItemRegistry.ARROW_BUNDLE));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, Wrapper recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.addTooltipCallback((slot, isInput, stack, tooltip) ->
        {
            if (!isInput) return;
            int ammoValue = recipeWrapper.components.get(slot - 1).getAmmoValue(stack);
            if (ammoValue == 0) // 0 means component is special (e.g potatosser coal)
                return;
            tooltip.add(I18n.format(LANG_PREFIX + ".ammo_value", ammoValue));
        });

        guiItemStacks.init(0, false, WIDTH - 22, HEIGHT / 2 - 9);
        guiItemStacks.set(0, Iterables.getOnlyElement(ingredients.getOutputs(VanillaTypes.ITEM)));

        List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
        int yOffset = (HEIGHT - inputs.size() * 18) / 2;
        for (int componentIndex = 0; componentIndex < inputs.size(); componentIndex++)
        {
            List<ItemStack> component = inputs.get(componentIndex);
            guiItemStacks.init(componentIndex + 1, true, 0, componentIndex * 18 + yOffset);
            guiItemStacks.set(componentIndex + 1, component);
        }
    }

    public static class Wrapper implements IRecipeWrapper
    {
        private final ItemStack target;
        private final List<ComponentData> components;
        private static IDrawable craftIcon, useMagazineIcon;
        private final IStackHelper stackHelper;

        public Wrapper(AmmoContainer target, ReloadSpecification wrapped, IJeiHelpers jeiHelpers)
        {
            this.target = target.createFull();
            this.components = new ArrayList<>(wrapped.getComponents());
            if (craftIcon == null) //Shared between all instances, instead of created every time
            {
                craftIcon = jeiHelpers.getGuiHelper().createDrawable(TEXTURE, WIDTH, 0, 13, 13);
                useMagazineIcon = jeiHelpers.getGuiHelper().createDrawable(TEXTURE, WIDTH, 13, 13, 13);
            }
            this.stackHelper = jeiHelpers.getStackHelper();
        }

        @Override
        public void getIngredients(IIngredients ingredients)
        {
            List<List<ItemStack>> inputs = stackHelper.expandRecipeItemStackInputs(components.stream()
                .map(ComponentData::getIngredient)
                .collect(toList()));
            ingredients.setInputLists(VanillaTypes.ITEM, inputs);
            ingredients.setOutput(VanillaTypes.ITEM, target);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
        {
            int actionIndicatorX = WIDTH / 2,
                actionIndicatorY = HEIGHT / 2 - 7;
            if (target.getItem() instanceof Weapon)
                craftIcon.draw(minecraft, actionIndicatorX, actionIndicatorY);
            else if (target.getItem() instanceof AmmoMagazine)
                useMagazineIcon.draw(minecraft, actionIndicatorX, actionIndicatorY);

            int yOffset = 6 + (recipeHeight - components.size() * 18) / 2;
            int inputIndex = 0;
            for (ComponentData component : components)
            {
                String quantity = component.getMin() == component.getMax()
                    ? I18n.format(LANG_PREFIX + ".quantity", component.getMin())
                    : I18n.format(LANG_PREFIX + ".quantity_range", component.getMin(), component.getMax());
                minecraft.fontRenderer.drawString(quantity, 20, inputIndex * 18 + yOffset, 0x000000);
                inputIndex += 1;
            }
        }

        @Override
        public List<String> getTooltipStrings(int mouseX, int mouseY)
        {
            int actionIndicatorX = WIDTH / 2 - 4,
                actionIndicatorY = HEIGHT / 2 - 7;
            if (mouseX >= actionIndicatorX && mouseX - actionIndicatorX <= 28 &&
                mouseY >= actionIndicatorY - 1 && mouseY - actionIndicatorY <= 13)
            {
                if (target.getItem() instanceof Weapon)
                    return Collections.singletonList(I18n.format(LANG_PREFIX + ".craft"));
                else if (target.getItem() instanceof AmmoMagazine)
                    return Collections.singletonList(I18n.format(LANG_PREFIX + ".use_magazine"));
            }
            return Collections.emptyList();
        }
    }

    @Override
    public String getUid()
    {
        return ID;
    }

    @Override
    public String getTitle()
    {
        return translatedTitle;
    }

    @Override
    public String getModName()
    {
        return QuiverbowMain.MODID;
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public IDrawable getIcon()
    {
        return tabIcon;
    }
}
