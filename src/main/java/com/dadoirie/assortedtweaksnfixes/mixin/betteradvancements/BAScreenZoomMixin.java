package com.dadoirie.assortedtweaksnfixes.mixin.betteradvancements;

import betteradvancements.common.gui.BetterAdvancementsScreen;
import com.dadoirie.assortedtweaksnfixes.utils.betteradvancements.BAZoom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BetterAdvancementsScreen.class)
public abstract class BAScreenZoomMixin {

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void onMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, CallbackInfoReturnable<Boolean> cir) {
        BAZoom.baZoom = Math.clamp(BAZoom.baZoom + (scrollY > 0 ? BAZoom.ZOOM_STEP : -BAZoom.ZOOM_STEP), BAZoom.MIN_ZOOM, BAZoom.MAX_ZOOM);
        cir.setReturnValue(true);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void resetZoomOnOpen(CallbackInfo ci) {
        BAZoom.baZoom = 1.0f;
    }
}