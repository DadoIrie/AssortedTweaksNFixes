package com.dadoirie.assortedtweaksnfixes.content.hammer.client;

import com.dadoirie.assortedtweaksnfixes.content.hammer.HammerFeature;
import com.dadoirie.assortedtweaksnfixes.content.hammer.HammerRecipe;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@ParametersAreNonnullByDefault
public class HammerJEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(HammerFeature.MOD_ID, "jei_plugin");
    private static final ResourceLocation HAMMERING_ID = ResourceLocation.fromNamespaceAndPath(HammerFeature.MOD_ID, "hammering");

    private CreateRecipeCategory<HammerRecipe> hammering;

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        hammering = new CreateRecipeCategory.Builder<>(HammerRecipe.class)
                .addTypedRecipes(HammerFeature.HAMMERING)
                .catalyst(HammerFeature.HAMMER::get)
                .doubleItemIcon(HammerFeature.HAMMER.get(), AllItems.IRON_SHEET.get())
                .emptyBackground(177, 55)
                .build(HAMMERING_ID, HammeringCategory::new);
        registration.addRecipeCategories(hammering);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        hammering.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        hammering.registerCatalysts(registration);
    }
}
