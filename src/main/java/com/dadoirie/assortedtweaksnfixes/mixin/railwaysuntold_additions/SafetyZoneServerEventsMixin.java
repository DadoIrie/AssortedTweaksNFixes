package com.dadoirie.assortedtweaksnfixes.mixin.railwaysuntold_additions;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModList;
import net.minecraft.server.level.ServerPlayer;
import com.vodmordia.railwaysuntold_additions.safetyzone.network.SafetyZoneServerEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;

@Mixin(SafetyZoneServerEvents.class)
public class SafetyZoneServerEventsMixin {

    @Inject(
            method = "syncToPlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void railwaysuntold_additions$skipWithoutRailwaysUntold(ServerPlayer player, CallbackInfo ci) {
        if (!ModList.get().isLoaded("railwaysuntold")) {
            ci.cancel();
        }
    }
}