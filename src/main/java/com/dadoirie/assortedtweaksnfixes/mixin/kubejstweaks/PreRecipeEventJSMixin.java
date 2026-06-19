package com.dadoirie.assortedtweaksnfixes.mixin.kubejstweaks;

import com.google.gson.JsonElement;
import dev.uncandango.kubejstweaks.kubejs.event.PreRecipeEventJS;
import com.dadoirie.assortedtweaksnfixes.mixin.kubejstweaks.prerecipeeventjs.PreRecipeEventJSAccessor;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Mixin(value = PreRecipeEventJS.class, remap = false)
public abstract class PreRecipeEventJSMixin {

    @Unique
    private Stream<PreRecipeEventJS.RecipeEntry> assortedtweaksnfixes$safeFilter(Predicate<PreRecipeEventJS.RecipeEntry> predicate) {
        Map<ResourceLocation, JsonElement> recipeJsons = ((PreRecipeEventJSAccessor) this).assortedtweaksnfixes$getRecipeJsons();
        return recipeJsons.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().isJsonObject())
                .map(entry -> new PreRecipeEventJS.RecipeEntry(entry.getKey(), entry.getValue().getAsJsonObject()))
                .filter(predicate);
    }

    @Inject(method = "filterByModId", at = @At("HEAD"), cancellable = true, remap = false)
    private void assortedtweaksnfixes$fixFilterByModId(String modId, CallbackInfoReturnable<Stream<PreRecipeEventJS.RecipeEntry>> cir) {
        cir.setReturnValue(assortedtweaksnfixes$safeFilter(recipe -> recipe.id().getNamespace().equals(modId)));
    }

    @Inject(method = "filterRegex", at = @At("HEAD"), cancellable = true, remap = false)
    private void assortedtweaksnfixes$fixFilterRegex(Pattern regex, CallbackInfoReturnable<Stream<PreRecipeEventJS.RecipeEntry>> cir) {
        cir.setReturnValue(assortedtweaksnfixes$safeFilter(recipe -> regex.asPredicate().test(recipe.id().toString())));
    }
}