package com.dadoirie.assortedtweaksnfixes.mixin.betteradvancements.bascreenzoom;

import betteradvancements.common.gui.BetterAdvancementTab;
import com.dadoirie.assortedtweaksnfixes.utils.betteradvancements.BAZoom;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BetterAdvancementTab.class)
public abstract class BATabDrawMixin {

    @Inject(
            method = "drawContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lbetteradvancements/common/gui/BetterAdvancementWidget;drawConnectivity(Lnet/minecraft/client/gui/GuiGraphics;IIZ)V",
                    ordinal = 0
            )
    )
    private void injectZoomScale(GuiGraphics guiGraphics, int left, int top, int width, int height, CallbackInfo ci) {
        float zoom = BAZoom.baZoom;
        guiGraphics.pose().translate(width / 2f, height / 2f, 0f);
        guiGraphics.pose().scale(zoom, zoom, 1.0f);
        guiGraphics.pose().translate(-width / 2f, -height / 2f, 0f);
    }
}