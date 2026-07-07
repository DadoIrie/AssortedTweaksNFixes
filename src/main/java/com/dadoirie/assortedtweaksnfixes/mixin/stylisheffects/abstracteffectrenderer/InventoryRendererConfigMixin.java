package com.dadoirie.assortedtweaksnfixes.mixin.stylisheffects.abstracteffectrenderer;

import com.dadoirie.assortedtweaksnfixes.utils.stylisheffects.InventoryRendererConfigExtensions;
import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.stylisheffects.config.ClientConfig;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientConfig.InventoryRendererConfig.class)
public class InventoryRendererConfigMixin implements InventoryRendererConfigExtensions {
    @Config(description = "Offset on x-axis for inventory view.")
    @Config.IntRange(min = 0)
    public int offsetX = 0;

    @Override
    public int stylishEffects$getOffsetX() {
        return this.offsetX;
    }
}