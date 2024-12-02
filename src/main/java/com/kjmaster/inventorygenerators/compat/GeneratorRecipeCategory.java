package com.kjmaster.inventorygenerators.compat;

import com.kjmaster.inventorygenerators.InventoryGenerators;
import com.kjmaster.inventorygenerators.recipe.GeneratorRecipe;
import com.kjmaster.inventorygenerators.utils.StringHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class GeneratorRecipeCategory implements IRecipeCategory<GeneratorRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public GeneratorRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, "textures/gui/jei/generator_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(InventoryGenerators.MODID, "inventory_furnace_generator"))));
    }

    @Override
    public RecipeType<GeneratorRecipe> getRecipeType() {
        return JEIPlugin.GENERATOR_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(InventoryGenerators.MODID + ".recipe.generator");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GeneratorRecipe recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 0, 41)
                .addItemStacks(Arrays.asList(recipe.generator().getItems()))
                .setSlotName("generator");
        builder.addSlot(RecipeIngredientRole.INPUT, 18, 41)
                .addItemStacks(recipe.fuels().stream().flatMap(ingredient -> Arrays.stream(ingredient.getItems())).toList())
                .setSlotName("fuels");
    }

    @Override
    public void draw(GeneratorRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.drawString(minecraft.font, "Rate: " + StringHelper.getScaledNumber(recipe.RF()) + "RF/t", 37F, 14F, 4210752, false);
        guiGraphics.drawString(minecraft.font, "Burntime: " + StringHelper.format(recipe.burnTime()), 37F, 32F, 4210752, false);
        guiGraphics.drawString(minecraft.font, "Total: " + StringHelper.getScaledNumber((long) recipe.RF() * recipe.burnTime()) + "RF", 37F, 50F, 4210752, false);
    }
}
