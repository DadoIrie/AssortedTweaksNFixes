package com.dadoirie.assortedtweaksnfixes.mixin.technonw.bankcardpaymentservice;

import com.technophobia.technonw.client.NumismaticsRequirementRenderer;
import com.technophobia.technonw.waystones.NumismaticsWarpRequirement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NumismaticsRequirementRenderer.class)
public class NumismaticsRequirementRendererMixin {

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void technonw$hideCurrentWaystone(Player player, NumismaticsWarpRequirement requirement, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, int anchorX, int anchorY, CallbackInfo ci) {
        if (requirement.getCost() < 0) {
            ci.cancel();
        }
    }
}