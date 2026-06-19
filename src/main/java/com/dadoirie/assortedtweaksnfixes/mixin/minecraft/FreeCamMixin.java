package com.dadoirie.assortedtweaksnfixes.mixin.minecraft;

import com.dadoirie.assortedtweaksnfixes.client.Freecam;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class FreeCamMixin {
    @Shadow protected abstract void setPosition(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "setup", at = @At("TAIL"))
    private void overridePositionIfFreecam(
            BlockGetter level, Entity entity, boolean detached,
            boolean thirdPersonReverse, float partialTick, CallbackInfo ci
    ) {
        if (Freecam.isActive()) {
            double lerpX = Mth.lerp(partialTick, Freecam.prevX, Freecam.x);
            double lerpY = Mth.lerp(partialTick, Freecam.prevY, Freecam.y);
            double lerpZ = Mth.lerp(partialTick, Freecam.prevZ, Freecam.z);
            this.setPosition(lerpX, lerpY, lerpZ);
            this.setRotation(Freecam.yaw, Freecam.pitch);
        }
    }

    @Inject(method = "isDetached", at = @At("HEAD"), cancellable = true)
    private void forceRenderPlayer(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isActive()) {
            cir.setReturnValue(true);
        }
    }
}