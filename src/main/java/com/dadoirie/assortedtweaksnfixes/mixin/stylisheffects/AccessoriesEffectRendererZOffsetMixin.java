package com.dadoirie.assortedtweaksnfixes.mixin.stylisheffects;

import fuzs.stylisheffects.client.gui.effects.AbstractEffectRenderer;
import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractEffectRenderer.class)
public abstract class AccessoriesEffectRendererZOffsetMixin {

    @Shadow
    protected Object screen;

    @Inject(method = "renderWidget", at = @At("HEAD"))
    private void onRenderWidgetHead(GuiGraphics guiGraphics, int posX, int posY, Minecraft minecraft, MobEffectInstance mobEffectInstance, CallbackInfo ci) {
        if (this.screen instanceof AccessoriesExperimentalScreen) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
        }
    }

    @Inject(method = "renderWidget", at = @At("RETURN"))
    private void onRenderWidgetReturn(GuiGraphics guiGraphics, int posX, int posY, Minecraft minecraft, MobEffectInstance mobEffectInstance, CallbackInfo ci) {
        if (this.screen instanceof AccessoriesExperimentalScreen) {
            guiGraphics.pose().popPose();
        }
    }
}