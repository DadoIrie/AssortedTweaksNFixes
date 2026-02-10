package com.dadoirie.assortedtweaksnfixes.mixin.accessories;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.mojang.blaze3d.platform.InputConstants;
import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import io.wispforest.accessories.client.AccessoriesClient;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class InventoryKeyMixin {
    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(InventoryKeyMixin.class);

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (AccessoriesClient.OPEN_SCREEN == null) return;
        InputConstants.Key key = InputConstants.getKey(keyCode, scanCode);
        if (AccessoriesClient.OPEN_SCREEN.isActiveAndMatches(key)) {
            Screen screen = mc.screen;
            if (screen instanceof AbstractContainerScreen && !(screen instanceof AccessoriesExperimentalScreen)) {
                screen.onClose();
                cir.setReturnValue(true);
            }
        }
    }
}
