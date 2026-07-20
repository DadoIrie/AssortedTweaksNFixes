package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.client;

import com.dadoirie.assortedtweaksnfixes.compat.etched.client.SoundContinuity;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.ContraptionRecordsData;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.api.sound.AbstractOnlineSoundInstance;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.api.sound.StopListeningSound;
import gg.moonflower.etched.api.sound.source.AudioSource;
import gg.moonflower.etched.common.blockentity.AlbumJukeboxBlockEntity;
import gg.moonflower.etched.core.Etched;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ContraptionSoundManager {

    private static final int ORPHAN_GRACE_TICKS = 40;
    // per-tick lerp factor toward the block's real position; filters Create's interpolation jitter that OpenAL turns into panning warble near moving sources; lower = smoother but sound trails the block further, higher = tighter tracking but more jitter audible, 1.0 = off
    private static final double POSITION_SMOOTHING = 0.50;
    // distance (blocks) below which the source blends toward the camera, fully head-centered at 0; kills hard left/right panning flips when standing on/next to the block; lower = stabilized zone shrinks, higher = directionality fades out earlier, 0.0 = off
    private static final double NEAR_FIELD_RADIUS = 2.0;
    private static final Component RADIO_TITLE = Component.translatable("sound_source." + Etched.MOD_ID + ".radio");

    private record SoundKey(int contraptionId, BlockPos localPos) {
    }

    private record LandedPlayback(SoundInstance sound, List<TrackData> tracks, int nextIndex) {
    }

    private static final class TrackedSound {
        SoundInstance sound;
        CompoundTag payload;
        List<TrackData> tracks;
        int trackIndex;
        int orphanTicks;
        boolean everActive;
        Vec3 smoothedPosition;

        TrackedSound(CompoundTag payload, List<TrackData> tracks) {
            this.payload = payload;
            this.tracks = tracks;
        }
    }

    private static final Map<SoundKey, TrackedSound> SOUNDS = new HashMap<>();
    private static final Map<BlockPos, LandedPlayback> LANDINGS = new HashMap<>();

    public static void reconcile(Entity contraptionEntity, List<ContraptionRecordsData.Entry> entries) {
        if (!(contraptionEntity.level() instanceof ClientLevel level)) {
            return;
        }

        int contraptionId = contraptionEntity.getId();
        Set<BlockPos> active = new HashSet<>();
        for (ContraptionRecordsData.Entry entry : entries) {
            active.add(entry.localPos());
            SoundKey key = new SoundKey(contraptionId, entry.localPos());
            TrackedSound current = SOUNDS.get(key);
            if (current == null) {
                start(level, key, entry);
            } else if (!Objects.equals(current.payload, entry.payload())
                    && !retarget(level.registryAccess(), current, entry.payload())) {
                SOUNDS.remove(key);
                SoundContinuity.stopInstance(current.sound);
                start(level, key, entry);
            }
        }
        SOUNDS.entrySet().removeIf(mapEntry -> {
            SoundKey key = mapEntry.getKey();
            if (key.contraptionId() != contraptionId || active.contains(key.localPos())) {
                return false;
            }
            SoundContinuity.stopInstance(mapEntry.getValue().sound);
            return true;
        });
    }

    public static void handoff(int contraptionId, BlockPos localPos, BlockPos landingPos) {
        TrackedSound tracked = SOUNDS.remove(new SoundKey(contraptionId, localPos));
        if (tracked == null) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            SoundContinuity.stopInstance(tracked.sound);
            return;
        }

        Vec3 position = Vec3.atCenterOf(landingPos);
        SoundContinuity.setPosition(tracked.sound, position);
        SoundInstance replaced = SoundContinuity.blockSounds().put(landingPos, tracked.sound);
        if (replaced != null && replaced != tracked.sound) {
            SoundContinuity.stopInstance(replaced);
        }

        if (!tracked.tracks.isEmpty()) {
            if (level.getBlockEntity(landingPos) instanceof AlbumJukeboxBlockEntity jukebox) {
                SoundContinuity.align(jukebox, tracked.trackIndex);
            }
            LANDINGS.put(landingPos, new LandedPlayback(tracked.sound, List.copyOf(tracked.tracks), tracked.trackIndex + 1));
        }
        partyNearbyEntities(level, position);
    }

    public static void stop(int contraptionId, BlockPos localPos) {
        TrackedSound tracked = SOUNDS.remove(new SoundKey(contraptionId, localPos));
        if (tracked != null) {
            SoundContinuity.stopInstance(tracked.sound);
        }
    }

    public static void reconnectRadios(ClientLevel level) {
        for (SoundKey key : List.copyOf(SOUNDS.keySet())) {
            TrackedSound tracked = SOUNDS.get(key);
            if (tracked == null || !tracked.payload.contains("url", Tag.TAG_STRING))
                continue;

            SOUNDS.remove(key);
            SoundContinuity.stopInstance(tracked.sound);
            playStream(level, key, tracked.payload);
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (SOUNDS.isEmpty() && LANDINGS.isEmpty()) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            SOUNDS.values().forEach(tracked -> SoundContinuity.stopInstance(tracked.sound));
            SOUNDS.clear();
            LANDINGS.clear();
            return;
        }

        tickContraptionSounds(level);
        tickLandedPlayback(level);
    }

    private static void tickContraptionSounds(ClientLevel level) {
        for (Iterator<Map.Entry<SoundKey, TrackedSound>> iterator = SOUNDS.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<SoundKey, TrackedSound> entry = iterator.next();
            SoundKey key = entry.getKey();
            TrackedSound tracked = entry.getValue();
            if (!(level.getEntity(key.contraptionId()) instanceof AbstractContraptionEntity contraption)
                    || !contraption.isAlive()) {
                if (++tracked.orphanTicks > ORPHAN_GRACE_TICKS) {
                    iterator.remove();
                    SoundContinuity.stopInstance(tracked.sound);
                }
                continue;
            }

            tracked.orphanTicks = 0;
            Vec3 position = contraption.toGlobalVector(Vec3.atCenterOf(key.localPos()), 1);
            SoundContinuity.setPosition(tracked.sound, audiblePosition(tracked, position));
            if (Minecraft.getInstance().getSoundManager().isActive(tracked.sound)) {
                tracked.everActive = true;
                partyNearbyEntities(level, position);
            } else if (tracked.everActive && !advance(level, key, tracked)) {
                iterator.remove();
            }
        }
    }

    private static void tickLandedPlayback(ClientLevel level) {
        for (Iterator<Map.Entry<BlockPos, LandedPlayback>> iterator = LANDINGS.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<BlockPos, LandedPlayback> entry = iterator.next();
            BlockPos pos = entry.getKey();
            LandedPlayback landed = entry.getValue();
            if (SoundContinuity.blockSounds().get(pos) != landed.sound()) {
                iterator.remove();
                continue;
            }
            if (Minecraft.getInstance().getSoundManager().isActive(landed.sound())) {
                continue;
            }

            iterator.remove();
            if (level.getBlockEntity(pos) instanceof AlbumJukeboxBlockEntity jukebox) {
                jukebox.next();
                SoundTracker.playAlbum(jukebox, jukebox.getBlockState(), level, pos, true);
            } else if (landed.nextIndex() < landed.tracks().size()) {
                SoundTracker.playBlockRecord(pos, landed.tracks().toArray(TrackData[]::new), landed.nextIndex());
            } else {
                SoundContinuity.blockSounds().remove(pos);
            }
        }
    }

    private static void start(ClientLevel level, SoundKey key, ContraptionRecordsData.Entry entry) {
        CompoundTag payload = entry.payload();
        List<TrackData> tracks = tracksOf(level.registryAccess(), payload);
        SoundInstance orphaned = SoundContinuity.blockSounds().remove(entry.originalPos());
        if (orphaned != null) {
            if (adopt(key, payload, tracks, orphaned)) {
                return;
            }
            SoundContinuity.stopInstance(orphaned);
        }

        if (payload.contains("url", Tag.TAG_STRING)) {
            playStream(level, key, payload);
            return;
        }

        TrackedSound tracked = new TrackedSound(payload, tracks);
        if (playTrack(level, key, tracked, 0)) {
            SOUNDS.put(key, tracked);
        }
    }

    private static boolean adopt(SoundKey key, CompoundTag payload, List<TrackData> tracks, SoundInstance sound) {
        int index = payload.contains("url", Tag.TAG_STRING)
                ? SoundContinuity.matches(sound, payload.getString("url")) ? 0 : -1
                : indexOfMatch(sound, tracks);
        if (index < 0) {
            return false;
        }

        if (sound instanceof StopListeningSound listening) {
            listening.stopListening();
        }
        TrackedSound tracked = new TrackedSound(payload, tracks);
        tracked.sound = sound;
        tracked.trackIndex = index;
        tracked.everActive = true;
        SOUNDS.put(key, tracked);
        return true;
    }

    private static boolean retarget(HolderLookup.Provider registries, TrackedSound tracked, CompoundTag newPayload) {
        List<TrackData> newTracks = tracksOf(registries, newPayload);
        int index = indexOfMatch(tracked.sound, newTracks);
        if (index < 0) {
            return false;
        }

        tracked.payload = newPayload;
        tracked.tracks = newTracks;
        tracked.trackIndex = index;
        return true;
    }

    private static boolean advance(ClientLevel level, SoundKey key, TrackedSound tracked) {
        int next = tracked.trackIndex + 1;
        if (next >= tracked.tracks.size()) {
            if (!tracked.payload.contains("items", Tag.TAG_LIST)) {
                return false;
            }
            next = 0;
        }
        return playTrack(level, key, tracked, next);
    }

    private static boolean playTrack(ClientLevel level, SoundKey key, TrackedSound tracked, int startIndex) {
        Vec3 position = currentPosition(level, key);
        if (position == null) {
            return false;
        }

        for (int index = startIndex; index < tracked.tracks.size(); index++) {
            TrackData track = tracked.tracks.get(index);
            AbstractOnlineSoundInstance sound = SoundTracker.getEtchedRecord(track.url(), track.getDisplayName(), level,
                    BlockPos.containing(position), 16, AudioSource.AudioFileType.FILE);
            if (sound == null) {
                continue;
            }

            tracked.sound = sound;
            tracked.trackIndex = index;
            tracked.everActive = false;
            SoundContinuity.setPosition(sound, position);
            Minecraft.getInstance().getSoundManager().play(sound);
            if (PlayableRecord.canShowMessage(position.x, position.y, position.z)) {
                Minecraft.getInstance().gui.setNowPlaying(track.getDisplayName());
            }
            return true;
        }
        return false;
    }

    private static void playStream(ClientLevel level, SoundKey key, CompoundTag payload) {
        Vec3 position = currentPosition(level, key);
        if (position == null) {
            return;
        }

        AbstractOnlineSoundInstance sound = SoundTracker.getEtchedRecord(payload.getString("url"), RADIO_TITLE, level,
                BlockPos.containing(position), 8, AudioSource.AudioFileType.BOTH);
        if (sound == null) {
            return;
        }

        sound.setLoop(true);
        TrackedSound tracked = new TrackedSound(payload, List.of());
        tracked.sound = sound;
        SOUNDS.put(key, tracked);
        SoundContinuity.setPosition(sound, position);
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    private static List<TrackData> tracksOf(HolderLookup.Provider registries, CompoundTag payload) {
        if (payload.contains("record", Tag.TAG_COMPOUND)) {
            ItemStack record = ItemStack.parse(registries, payload.getCompound("record")).orElse(ItemStack.EMPTY);
            return PlayableRecord.isPlayableRecord(record) ? PlayableRecord.getTracks(registries, record) : List.of();
        }
        if (payload.contains("items", Tag.TAG_LIST)) {
            List<TrackData> tracks = new ArrayList<>();
            for (Tag element : payload.getList("items", Tag.TAG_COMPOUND)) {
                ItemStack stack = ItemStack.parse(registries, (CompoundTag) element).orElse(ItemStack.EMPTY);
                if (PlayableRecord.isPlayableRecord(stack)) {
                    tracks.addAll(PlayableRecord.getTracks(registries, stack));
                }
            }
            return tracks;
        }
        return List.of();
    }

    private static int indexOfMatch(SoundInstance sound, List<TrackData> tracks) {
        for (int index = 0; index < tracks.size(); index++) {
            if (SoundContinuity.matches(sound, tracks.get(index).url())) {
                return index;
            }
        }
        return -1;
    }

    private static Vec3 audiblePosition(TrackedSound tracked, Vec3 target) {
        tracked.smoothedPosition = tracked.smoothedPosition == null
                ? target
                : tracked.smoothedPosition.lerp(target, POSITION_SMOOTHING);

        Vec3 listener = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double distance = tracked.smoothedPosition.distanceTo(listener);
        return distance >= NEAR_FIELD_RADIUS
                ? tracked.smoothedPosition
                : listener.lerp(tracked.smoothedPosition, distance / NEAR_FIELD_RADIUS);
    }

    private static Vec3 currentPosition(ClientLevel level, SoundKey key) {
        if (level.getEntity(key.contraptionId()) instanceof AbstractContraptionEntity contraption && contraption.isAlive()) {
            return contraption.toGlobalVector(Vec3.atCenterOf(key.localPos()), 1);
        }
        return null;
    }

    private static void partyNearbyEntities(ClientLevel level, Vec3 position) {
        BlockPos pos = BlockPos.containing(position);
        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(3.45))) {
            living.setRecordPlayingNearby(pos, true);
        }
    }
}