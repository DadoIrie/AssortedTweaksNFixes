package com.dadoirie.assortedtweaksnfixes.mixin.railways;

import com.railwayteam.railways.registry.CRInteractionBehaviours;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CRInteractionBehaviours.class)
public class CRInteractionBehavioursMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void atnf$cancelRegistration(CallbackInfo ci) {
        ci.cancel();
    }
}
