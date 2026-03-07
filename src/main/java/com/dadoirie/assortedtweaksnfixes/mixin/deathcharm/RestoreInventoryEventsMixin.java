package com.dadoirie.assortedtweaksnfixes.mixin.deathcharm;

import dev.livaco.deathcharm.events.RestoreInventoryEvents;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels all RestoreInventoryEvents methods so that DeathCharmCompat fully controls inventory restoration.
 */
@Mixin(RestoreInventoryEvents.class)
public class RestoreInventoryEventsMixin {

    @Inject(method = "onLivingDeath", at = @At("HEAD"), cancellable = true)
    private void cancelOnLivingDeath(LivingDeathEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onLivingDrops", at = @At("HEAD"), cancellable = true)
    private void cancelOnLivingDrops(LivingDropsEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "onPlayerRespawn", at = @At("HEAD"), cancellable = true)
    private void cancelOnPlayerRespawn(PlayerEvent.PlayerRespawnEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}