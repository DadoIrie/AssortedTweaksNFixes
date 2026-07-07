package com.dadoirie.assortedtweaksnfixes.mixin.stylisheffects;

import com.dadoirie.assortedtweaksnfixes.utils.stylisheffects.InventoryRendererConfigExtensions;
import fuzs.stylisheffects.api.v1.client.MobEffectWidgetContext;
import fuzs.stylisheffects.client.gui.effects.AbstractEffectRenderer;
import fuzs.stylisheffects.client.handler.EffectRendererEnvironment;
import fuzs.stylisheffects.config.ClientConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractEffectRenderer.class)
public abstract class AbstractEffectRendererMixin {
    @Final
    @Shadow private EffectRendererEnvironment environment;
    @Shadow private int startX;
    @Shadow private int availableWidth;
    @Shadow private MobEffectWidgetContext.ScreenSide screenSide;
    @Shadow protected abstract ClientConfig.EffectRendererConfig rendererConfig();

    @Inject(method = "setScreenDimensions", at = @At("TAIL"))
    private void onSetScreenDimensions(Object screen, int availableWidth, int availableHeight, int startX, int startY, MobEffectWidgetContext.ScreenSide screenSide, CallbackInfo ci) {
        if (this.environment == EffectRendererEnvironment.INVENTORY) {
            int offset = ((InventoryRendererConfigExtensions) this.rendererConfig()).stylishEffects$getOffsetX();
            this.availableWidth -= offset;
            this.startX += (this.screenSide == MobEffectWidgetContext.ScreenSide.RIGHT ? 1 : -1) * offset;
        }
    }
}