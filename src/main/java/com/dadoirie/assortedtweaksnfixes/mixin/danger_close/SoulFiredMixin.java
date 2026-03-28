package com.dadoirie.assortedtweaksnfixes.mixin.danger_close;

import com.cursee.danger_close.core.optional.SoulFired;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SoulFired.class, remap = false)
public class SoulFiredMixin {

    @Inject(method = "immolateSoul", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void immolateSoul(LivingEntity entity, CallbackInfo ci) {
        FireManager.setOnFire(entity, 2.0F, FireManager.SOUL_FIRE_TYPE);
        ci.cancel();
    }

    @Inject(method = "spreadTypedFire", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void spreadTypedFire(LivingEntity entityA, LivingEntity entityB, CallbackInfo ci) {
        if (entityA.isOnFire() && !entityB.isOnFire()) {
            FireManager.setOnFire(entityB, 2.0F, ((FireTyped) entityA).getFireType());
        } else if (!entityA.isOnFire() && entityB.isOnFire()) {
            FireManager.setOnFire(entityA, 2.0F, ((FireTyped) entityB).getFireType());
        }
        ci.cancel();
    }
}