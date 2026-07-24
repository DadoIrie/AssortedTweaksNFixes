package com.dadoirie.assortedtweaksnfixes.mixin.createconnected.ccmovementbehaviours;

import com.hlysine.create_connected.registries.CCInteractionBehaviours;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CCInteractionBehaviours.class)
public class CCInteractionBehavioursMixin {

    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void atnf$cancelRegistration(CallbackInfo ci) {
        ci.cancel();
    }
}
