package com.dadoirie.assortedtweaksnfixes.mixin.accessories;

import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = AccessoriesExperimentalScreen.class, remap = false)
public class FixLayoutZIndexMixin {

    @ModifyArg(
            method = "build",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/wispforest/owo/ui/core/Component;zIndex(I)Lio/wispforest/owo/ui/core/Component;"
            ),
            index = 0
    )
    private int clampLayoutZIndex(int zIndex) {
        if (zIndex >= 200) {
            return 10;
        }
        return zIndex;
    }
}