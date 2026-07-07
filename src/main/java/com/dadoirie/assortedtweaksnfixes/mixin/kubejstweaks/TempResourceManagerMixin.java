package com.dadoirie.assortedtweaksnfixes.mixin.kubejstweaks;

import dev.uncandango.kubejstweaks.impl.TempResourceManager;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(TempResourceManager.class)
public abstract class TempResourceManagerMixin {

    @ModifyVariable(
            method = "<init>(Lnet/minecraft/server/packs/PackType;Ljava/util/List;)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private static List<PackResources> filterPacksEarly(List<PackResources> packs) {
        return packs.stream()
                .filter(pack -> !pack.packId().contains("excavated_variants"))
                .toList();
    }
}