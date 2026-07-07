package com.dadoirie.assortedtweaksnfixes.mixin.betteradvancements.bascreenzoom;

import betteradvancements.common.gui.BetterAdvancementTab;
import betteradvancements.common.gui.BetterAdvancementWidget;
import com.dadoirie.assortedtweaksnfixes.utils.betteradvancements.BAZoom;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BetterAdvancementTab.class)
public abstract class BATabTooltipMixin {

    @Unique private int tooltipWidth;
    @Unique private int tooltipHeight;

    @Inject(method = "drawToolTips", at = @At("HEAD"))
    private void captureViewportSize(GuiGraphics guiGraphics, int mouseX, int mouseY, int left, int top, int width, int height, CallbackInfo ci) {
        tooltipWidth = width;
        tooltipHeight = height;
    }

    // map mouse from screen space back to widget space accounting for pivot-centered zoom
    @Redirect(
            method = "drawToolTips",
            at = @At(
                    value = "INVOKE",
                    target = "Lbetteradvancements/common/gui/BetterAdvancementWidget;isMouseOver(DDDD)Z"
            )
    )
    private boolean redirectIsMouseOver(BetterAdvancementWidget widget, double scrollX, double scrollY, double mouseX, double mouseY) {
        float zoom = BAZoom.baZoom;
        double adjustedMouseX = (mouseX - tooltipWidth / 2.0) / zoom + tooltipWidth / 2.0;
        double adjustedMouseY = (mouseY - tooltipHeight / 2.0) / zoom + tooltipHeight / 2.0;
        return widget.isMouseOver(scrollX, scrollY, adjustedMouseX, adjustedMouseY);
    }

    // scale the tooltip pose to match widget zoom so tooltip tracks the widget visually
    @Inject(
            method = "drawToolTips",
            at = @At(
                    value = "INVOKE",
                    target = "Lbetteradvancements/common/gui/BetterAdvancementWidget;drawHover(Lnet/minecraft/client/gui/GuiGraphics;IIFII)V",
                    remap = false
            )
    )
    private void injectTooltipScale(GuiGraphics guiGraphics, int mouseX, int mouseY, int left, int top, int width, int height, CallbackInfo ci) {
        float zoom = BAZoom.baZoom;
        guiGraphics.pose().translate(tooltipWidth / 2f, tooltipHeight / 2f, 0f);
        guiGraphics.pose().scale(zoom, zoom, 1.0f);
        guiGraphics.pose().translate(-tooltipWidth / 2f, -tooltipHeight / 2f, 0f);
    }
}