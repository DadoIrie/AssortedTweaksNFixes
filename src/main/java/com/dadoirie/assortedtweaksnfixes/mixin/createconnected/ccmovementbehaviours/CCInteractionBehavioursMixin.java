package com.dadoirie.assortedtweaksnfixes.mixin.createconnected.ccmovementbehaviours;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "com.hlysine.create_connected.registries.CCInteractionBehaviours")
public class CCInteractionBehavioursMixin {

    /**
     * @author Damir Krupic
     * @reason ATNF replaces Create Connected's contraption interaction behaviours with its own
     * etched-aware implementations. Create Connected also skips its menu block registrations when
     * Steam 'n' Rails is present, but the unofficial NeoForge fork of Steam 'n' Rails ships no such
     * registrations, leaving a feature hole. ATNF will instead register menu blocks via instanceof
     * checks so modded variants of crafting tables etc. work on contraptions as well.
     */
    @Overwrite
    public static void register() {
    }
}
