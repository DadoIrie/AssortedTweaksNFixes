package com.dadoirie.assortedtweaksnfixes.content.hammer;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class HammerRecipe extends StandardProcessingRecipe<HammerRecipe.HammerInv> {

    public HammerRecipe(ProcessingRecipeParams params) {
        super(HammerFeature.HAMMERING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    public boolean matches(HammerInv input, @NotNull Level level) {
        return ingredients.getFirst().test(input.getItem(0));
    }

    public static class HammerInv extends RecipeWrapper {

        public HammerInv(ItemStack stack) {
            super(new ItemStackHandler(1));
            inv.insertItem(0, stack, false);
        }
    }
}
