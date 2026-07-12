package com.dadoirie.assortedtweaksnfixes.mixin.petrochem.voltedelectrolyzer;

import com.dadoirie.assortedtweaksnfixes.compat.electroenergetics.ATNFElectrolyzingKubeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import io.github.hadron13.petrochem.compat.kubejs.KubeJSPetrochemPlugin;
import io.github.hadron13.petrochem.register.PetrochemRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = KubeJSPetrochemPlugin.class, remap = false)
public abstract class KubeJSPetrochemPluginMixin {

    @Redirect(method = "registerRecipeSchemas",
            at = @At(value = "INVOKE",
                    target = "Ldev/latvian/mods/kubejs/recipe/schema/RecipeSchemaRegistry;register(Lnet/minecraft/resources/ResourceLocation;Ldev/latvian/mods/kubejs/recipe/schema/RecipeSchema;)V"))
    private void atf_swapElectrolyzingSchema(RecipeSchemaRegistry registry, ResourceLocation id, RecipeSchema schema) {
        if (id.equals(PetrochemRecipeTypes.ELECTROLYZING.id)) {
            registry.register(id, ATNFElectrolyzingKubeSchema.ELECTROLYZING_ELECTRIC_SCHEMA);
            return;
        }
        registry.register(id, schema);
    }
}
