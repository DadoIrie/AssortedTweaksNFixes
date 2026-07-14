package com.dadoirie.assortedtweaksnfixes.compat.etched;

import com.dadoirie.assortedtweaksnfixes.mixin.etched.AbstractSoundInstanceAccessor;
import gg.moonflower.etched.api.sound.StopListeningSound;
import gg.moonflower.etched.api.sound.WrappedSoundInstance;
import gg.moonflower.etched.core.mixin.client.render.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public class ContraptionSoundClientHandler {

    public static void handleStop(ClientboundJukeboxStopPacket packet, IPayloadContext context) {
        Map<BlockPos, SoundInstance> playingRecords = playingRecords();

        SoundInstance sound = playingRecords.remove(packet.pos());
        if (sound == null)
            return;

        if (sound instanceof StopListeningSound stopListening) {
            stopListening.stopListening();
        }
        Minecraft.getInstance().getSoundManager().stop(sound);
    }

    public static void handleLanded(ClientboundJukeboxLandedPacket packet, IPayloadContext context) {
        Map<BlockPos, SoundInstance> playingRecords = playingRecords();

        SoundInstance sound = playingRecords.remove(packet.originalPos());
        if (sound == null)
            return;

        setPosition(sound, Vec3.atCenterOf(packet.landingPos()));
        playingRecords.put(packet.landingPos(), sound);
    }

    public static void repositionTracked(BlockPos originalPos, Vec3 newPos) {
        SoundInstance sound = playingRecords().get(originalPos);
        if (sound == null)
            return;

        setPosition(sound, newPos);
    }

    public static Map<BlockPos, SoundInstance> playingRecords() {
        return ((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).getPlayingJukeboxSongs();
    }

    private static void setPosition(SoundInstance sound, Vec3 pos) {
        while (sound instanceof WrappedSoundInstance wrapped) {
            sound = wrapped.getParent();
        }

        if (!(sound instanceof AbstractSoundInstanceAccessor accessor))
            return;

        accessor.atnf$setX(pos.x);
        accessor.atnf$setY(pos.y);
        accessor.atnf$setZ(pos.z);
    }
}