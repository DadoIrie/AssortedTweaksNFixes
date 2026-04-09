package com.dadoirie.assortedtweaksnfixes.mixin.moonlight;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(
        targets = "net.mehvahdjukaar.moonlight.api.resources.textures.Respriter$FrameColorRemapper",
        remap = false
)
public interface RespriterMixin {

    @ModifyConstant(
            method = "of",
            constant = @Constant(intValue = 50),
            remap = false
    )
    private static int increaseMaxRecolorSize(int original) {
        return 500;
    }
}