package com.dadoirie.assortedtweaksnfixes.mixin.minecraft.freecam;

import com.dadoirie.assortedtweaksnfixes.client.Freecam;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void redirectMouseToCamera(CallbackInfo ci) {
        if (Freecam.isActive()) {
            MouseHandler mh = (MouseHandler)(Object)this;
            Freecam.yaw += (float) (mh.getXVelocity() * 0.15);
            Freecam.pitch += (float) (mh.getYVelocity() * 0.15);
            ci.cancel();
        }
    }
}