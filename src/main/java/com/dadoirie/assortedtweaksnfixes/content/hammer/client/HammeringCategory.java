package com.dadoirie.assortedtweaksnfixes.content.hammer.client;

import com.dadoirie.assortedtweaksnfixes.content.hammer.HammerFeature;
import com.dadoirie.assortedtweaksnfixes.content.hammer.HammerRecipe;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemComponent;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class HammeringCategory extends CreateRecipeCategory<HammerRecipe> {

    private final ItemStack renderedHammer = HammerFeature.HAMMER.toStack();

    public HammeringCategory(Info<HammerRecipe> info) {
        super(info);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, HammerRecipe recipe, @NotNull IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT, 27, 29)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().getFirst());

        ProcessingOutput output = recipe.getRollableResults().getFirst();
        builder
                .addSlot(RecipeIngredientRole.OUTPUT, 132, 29)
                .setBackground(getRenderedSlot(output), -1, -1)
                .addItemStack(output.getStack())
                .addRichTooltipCallback(addStochasticTooltip(output));
    }

    @Override
    protected void draw(HammerRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 21);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 32);

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        ItemStack[] matchingStacks = ingredients.getFirst().getItems();
        if (matchingStacks.length == 0)
            return;

        renderedHammer.set(HammerFeature.PROCESSING_ITEM.get(), new SandPaperItemComponent(matchingStacks[0]));
        GuiGameElement.of(renderedHammer)
                .<GuiGameElement.GuiRenderBuilder>at((float) getBackground().getWidth() / 2 - 16, 0, 0)
                .scale(2)
                .render(graphics);
    }
}