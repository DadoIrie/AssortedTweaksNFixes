package com.dadoirie.assortedtweaksnfixes.mixin.createconnected;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "com.hlysine.create_connected.registries.CCMovementBehaviours")
public class CCMovementBehavioursMixin {

    /**
     * @author Damir Krupic
     * @reason ATNF replaces Create Connected's contraption movement behaviours with its own
     * etched-aware implementations.
     */
    @Overwrite
    public static void register() {
    }
}
