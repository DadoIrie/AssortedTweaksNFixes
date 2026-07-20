package com.dadoirie.assortedtweaksnfixes.compat.etched.client;

import com.dadoirie.assortedtweaksnfixes.mixin.etched.AbstractSoundInstanceAccessor;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.api.sound.StopListeningSound;
import gg.moonflower.etched.api.sound.WrappedSoundInstance;
import gg.moonflower.etched.common.block.AlbumJukeboxBlock;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.common.blockentity.AlbumJukeboxBlockEntity;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.mixin.client.render.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class SoundContinuity {

    private SoundContinuity() {
    }

    public static Map<BlockPos, SoundInstance> blockSounds() {
        return ((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).getPlayingJukeboxSongs();
    }

    public static boolean matches(SoundInstance sound, String url) {
        ResourceLocation location = unwrap(sound).getLocation();
        return location.equals(Etched.etchedPath(DigestUtils.sha1Hex(url)))
                || location.equals(ResourceLocation.tryParse(url));
    }

    public static boolean resumeRadio(@Nullable String url, BlockState state, BlockPos pos) {
        if (!state.hasProperty(RadioBlock.POWERED) || state.getValue(RadioBlock.POWERED)
                || !TrackData.isValidURL(url)) {
            return false;
        }

        SoundInstance sound = activeSound(pos);
        return sound != null && matches(sound, url);
    }

    public static boolean resumeAlbum(AlbumJukeboxBlockEntity jukebox, BlockState state, BlockPos pos) {
        if (!state.hasProperty(AlbumJukeboxBlock.POWERED) || state.getValue(AlbumJukeboxBlock.POWERED)
                || jukebox.getLevel() == null) {
            return false;
        }

        SoundInstance sound = activeSound(pos);
        if (sound == null) {
            return false;
        }

        HolderLookup.Provider registries = jukebox.getLevel().registryAccess();
        for (int slot = 0; slot < jukebox.getContainerSize(); slot++) {
            List<TrackData> tracks = PlayableRecord.getTracks(registries, jukebox.getItem(slot));
            for (int track = 0; track < tracks.size(); track++) {
                if (matches(sound, tracks.get(track).url())) {
                    jukebox.setPlayingIndex(slot, track);
                    return true;
                }
            }
        }
        return false;
    }

    public static void align(AlbumJukeboxBlockEntity jukebox, int flatTrackIndex) {
        if (jukebox.getLevel() == null) {
            return;
        }

        HolderLookup.Provider registries = jukebox.getLevel().registryAccess();
        for (int slot = 0; slot < jukebox.getContainerSize(); slot++) {
            int count = PlayableRecord.getTrackCount(registries, jukebox.getItem(slot));
            if (flatTrackIndex < count) {
                jukebox.setPlayingIndex(slot, flatTrackIndex);
                return;
            }
            flatTrackIndex -= count;
        }
    }

    public static void setPosition(SoundInstance sound, Vec3 position) {
        if (unwrap(sound) instanceof AbstractSoundInstanceAccessor accessor) {
            accessor.atnf$setX(position.x);
            accessor.atnf$setY(position.y);
            accessor.atnf$setZ(position.z);
        }
    }

    public static void stopInstance(SoundInstance sound) {
        if (sound instanceof StopListeningSound stopListening) {
            stopListening.stopListening();
        }
        Minecraft.getInstance().getSoundManager().stop(sound);
    }

    @Nullable
    private static SoundInstance activeSound(BlockPos pos) {
        SoundInstance sound = blockSounds().get(pos);
        return sound != null && Minecraft.getInstance().getSoundManager().isActive(sound) ? sound : null;
    }

    private static SoundInstance unwrap(SoundInstance sound) {
        while (sound instanceof WrappedSoundInstance wrapped) {
            sound = wrapped.getParent();
        }
        return sound;
    }
}
