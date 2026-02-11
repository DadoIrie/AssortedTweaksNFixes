package com.dadoirie.assortedtweaksnfixes.mixin.travelersbackpack;

import com.tiviacz.travelersbackpack.inventory.upgrades.jukebox.JukeboxWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.api.record.PlayableRecord;

@Mixin(JukeboxWidget.class)
public abstract class JukeboxWidgetMixin {
    @Unique
    private ItemStack assortedTweaks$lastPlayedStack = null;

    @Inject(
        method = "getFromDisk",
        at = @At("HEAD"),
        cancellable = true
    )
    private void assortedTweaks$handleEtchedDisc(ItemStack stack, CallbackInfoReturnable<JukeboxSong> cir) {
        assortedTweaks$lastPlayedStack = stack.copy();
        
        if (!stack.has(DataComponents.JUKEBOX_PLAYABLE) && PlayableRecord.isPlayableRecord(stack)) {
            cir.setReturnValue(null);
        }
    }

    @Inject(
        method = "playDiscToPlayer",
        at = @At("HEAD"),
        cancellable = true
    )
    private void assortedTweaks$playEtchedDisc(int entityId, @Nullable JukeboxSong jukeboxSong, CallbackInfo ci) {
        if (jukeboxSong != null || assortedTweaks$lastPlayedStack == null) {
            return;
        }
        
        if (!assortedTweaks$lastPlayedStack.has(DataComponents.JUKEBOX_PLAYABLE) &&
            PlayableRecord.isPlayableRecord(assortedTweaks$lastPlayedStack)) {
            
            SoundTracker.playEntityRecord(
                assortedTweaks$lastPlayedStack,
                entityId,
                0,
                16,
                true
            );
            
            ci.cancel();
        }
        
        assortedTweaks$lastPlayedStack = null;
    }

    @Inject(
        method = "stopDisc",
        at = @At("HEAD"),
        cancellable = true
    )
    private void assortedTweaks$stopEtchedDisc(@Nullable JukeboxSong jukeboxSong, CallbackInfo ci) {
        if (jukeboxSong == null) {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                SoundTracker.setEntitySound(player.getId(), null);
            }
            ci.cancel();
        }
    }
}
