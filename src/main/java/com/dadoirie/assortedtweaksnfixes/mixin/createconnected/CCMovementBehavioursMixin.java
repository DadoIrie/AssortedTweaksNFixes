package com.dadoirie.assortedtweaksnfixes.mixin.createconnected;

import com.hlysine.create_connected.registries.CCMovementBehaviours;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CCMovementBehaviours.class)
public class CCMovementBehavioursMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void atnf$cancelRegistration(CallbackInfo ci) {
        ci.cancel();
    }
}
