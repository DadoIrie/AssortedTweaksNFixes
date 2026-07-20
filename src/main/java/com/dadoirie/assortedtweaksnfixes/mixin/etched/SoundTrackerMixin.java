package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import com.dadoirie.assortedtweaksnfixes.compat.etched.client.SoundContinuity;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.common.blockentity.AlbumJukeboxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundTracker.class)
public abstract class SoundTrackerMixin {

    @Inject(method = "playRadio", at = @At("HEAD"), cancellable = true)
    private static void atnf$resumeRadio(@Nullable String url, BlockState state, CommonLevelAccessor level,
                                         BlockPos pos, CallbackInfo ci) {
        if (SoundContinuity.resumeRadio(url, state, pos)) {
            ci.cancel();
        }
    }

    @Inject(method = "playAlbum", at = @At("HEAD"), cancellable = true)
    private static void atnf$resumeAlbum(AlbumJukeboxBlockEntity jukebox, BlockState state, CommonLevelAccessor level,
                                         BlockPos pos, boolean force, CallbackInfo ci) {
        if (!force && SoundContinuity.resumeAlbum(jukebox, state, pos)) {
            ci.cancel();
        }
    }
}