package com.dadoirie.assortedtweaksnfixes.mixin.kubejstweaks.prerecipeeventjs;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import dev.uncandango.kubejstweaks.kubejs.event.PreRecipeEventJS;

import java.util.Map;

@Mixin(value = PreRecipeEventJS.class, remap = false)
public interface PreRecipeEventJSAccessor {

    @Accessor("recipeJsons")
    Map<ResourceLocation, JsonElement> assortedtweaksnfixes$getRecipeJsons();
}