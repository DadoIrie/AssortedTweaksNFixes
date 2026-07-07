package com.dadoirie.assortedtweaksnfixes.mixin.betteradvancements.bascreenzoom;

import betteradvancements.common.gui.BetterAdvancementTab;
import betteradvancements.common.gui.BetterAdvancementsScreen;
import com.dadoirie.assortedtweaksnfixes.utils.betteradvancements.BAZoom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BetterAdvancementsScreen.class)
public abstract class BAScreenDragMixin {

    @Redirect(
            method = "mouseDragged",
            at = @At(
                    value = "INVOKE",
                    target = "Lbetteradvancements/common/gui/BetterAdvancementTab;scroll(DDII)V"
            )
    )
    private void redirectScroll(BetterAdvancementTab tab, double scrollX, double scrollY, int width, int height) {
        float zoom = BAZoom.baZoom;
        tab.scroll(scrollX / zoom, scrollY / zoom, width, height);
    }
}