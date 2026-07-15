package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.client;

import com.dadoirie.assortedtweaksnfixes.mixin.etched.AbstractSoundInstanceAccessor;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.api.sound.AbstractOnlineSoundInstance;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.api.sound.StopListeningSound;
import gg.moonflower.etched.api.sound.WrappedSoundInstance;
import gg.moonflower.etched.api.sound.source.AudioSource;
import gg.moonflower.etched.core.Etched;
import gg.moonflower.etched.core.mixin.client.render.LevelRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.*;

public class ContraptionSoundManager {

    private static final int ORPHAN_GRACE_TICKS = 40;
    private static final Component RADIO_TITLE = Component.translatable("sound_source." + Etched.MOD_ID + ".radio");

    public record RecordEntry(BlockPos localPos, BlockPos originalPos, CompoundTag payload) {
    }

    private record SoundKey(int contraptionId, BlockPos localPos) {
    }

    private static final class TrackedSound {
        SoundInstance sound;
        final CompoundTag payload;
        int orphanTicks;

        TrackedSound(SoundInstance sound, CompoundTag payload) {
            this.sound = sound;
            this.payload = payload;
        }
    }

    private static final Map<SoundKey, TrackedSound> SOUNDS = new HashMap<>();

    public static void reconcile(Entity contraptionEntity, List<RecordEntry> entries) {
        int contraptionId = contraptionEntity.getId();
        Set<BlockPos> active = new HashSet<>();
        for (RecordEntry entry : entries) {
            active.add(entry.localPos());
            SoundKey key = new SoundKey(contraptionId, entry.localPos());
            TrackedSound current = SOUNDS.get(key);
            if (current == null) {
                start(key, entry);
            } else if (!Objects.equals(current.payload, entry.payload())) {
                SOUNDS.remove(key);
                stopInstance(current.sound);
                start(key, entry);
            }
        }
        SOUNDS.entrySet().removeIf(mapEntry -> {
            SoundKey key = mapEntry.getKey();
            if (key.contraptionId() != contraptionId || active.contains(key.localPos())) {
                return false;
            }
            stopInstance(mapEntry.getValue().sound);
            return true;
        });
    }

    public static void handoff(int contraptionId, BlockPos localPos, BlockPos landingPos) {
        TrackedSound tracked = SOUNDS.remove(new SoundKey(contraptionId, localPos));
        if (tracked == null) {
            return;
        }
        setPosition(tracked.sound, Vec3.atCenterOf(landingPos));
        blockSounds().put(landingPos, tracked.sound);

        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            partyNearbyEntities(level, Vec3.atCenterOf(landingPos));
        }
    }

    public static void stop(int contraptionId, BlockPos localPos) {
        TrackedSound tracked = SOUNDS.remove(new SoundKey(contraptionId, localPos));
        if (tracked != null) {
            stopInstance(tracked.sound);
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (SOUNDS.isEmpty()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            SOUNDS.clear();
            return;
        }
        for (Iterator<Map.Entry<SoundKey, TrackedSound>> iterator = SOUNDS.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<SoundKey, TrackedSound> entry = iterator.next();
            TrackedSound tracked = entry.getValue();
            Entity entity = level.getEntity(entry.getKey().contraptionId());
            if (entity instanceof AbstractContraptionEntity contraption && contraption.isAlive()) {
                tracked.orphanTicks = 0;
                Vec3 position = contraption.toGlobalVector(Vec3.atCenterOf(entry.getKey().localPos()), 1);
                setPosition(tracked.sound, position);
                if (Minecraft.getInstance().getSoundManager().isActive(tracked.sound)) {
                    partyNearbyEntities(level, position);
                }
            } else if (++tracked.orphanTicks > ORPHAN_GRACE_TICKS) {
                iterator.remove();
                stopInstance(tracked.sound);
            }
        }
    }

    private static void partyNearbyEntities(ClientLevel level, Vec3 position) {
        BlockPos pos = BlockPos.containing(position);
        for (LivingEntity living : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(3.45))) {
            living.setRecordPlayingNearby(pos, true);
        }
    }

    private static void start(SoundKey key, RecordEntry entry) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        CompoundTag payload = entry.payload();
        SoundInstance existing = blockSounds().remove(entry.originalPos());
        if (existing != null) {
            SOUNDS.put(key, new TrackedSound(existing, payload));
            return;
        }
        if (payload.contains("url", Tag.TAG_STRING)) {
            playStream(key, level, payload.getString("url"), payload);
        } else if (payload.contains("record", Tag.TAG_COMPOUND)) {
            ItemStack record = ItemStack.parse(level.registryAccess(), payload.getCompound("record")).orElse(ItemStack.EMPTY);
            playRecord(key, level, record, payload);
        } else if (payload.contains("items", Tag.TAG_LIST)) {
            playPlaylist(key, level, payload.getList("items", Tag.TAG_COMPOUND), payload);
        }
    }

    private static void playPlaylist(SoundKey key, ClientLevel level, ListTag items, CompoundTag payload) {
        List<TrackData> tracks = new ArrayList<>();
        for (Tag element : items) {
            ItemStack stack = ItemStack.parse(level.registryAccess(), (CompoundTag) element).orElse(ItemStack.EMPTY);
            if (PlayableRecord.isPlayableRecord(stack)) {
                tracks.addAll(PlayableRecord.getTracks(level.registryAccess(), stack));
            }
        }
        if (!tracks.isEmpty()) {
            playTrack(key, tracks, 0, payload);
        }
    }

    private static void playRecord(SoundKey key, ClientLevel level, ItemStack record, CompoundTag payload) {
        if (!PlayableRecord.isPlayableRecord(record)) {
            return;
        }
        List<TrackData> tracks = PlayableRecord.getTracks(level.registryAccess(), record);
        if (!tracks.isEmpty()) {
            playTrack(key, tracks, 0, payload);
        }
    }

    private static void playStream(SoundKey key, ClientLevel level, String url, CompoundTag payload) {
        Vec3 position = currentPosition(level, key);
        if (position == null) {
            return;
        }
        AbstractOnlineSoundInstance sound = SoundTracker.getEtchedRecord(url, RADIO_TITLE, level,
                BlockPos.containing(position), 8, AudioSource.AudioFileType.BOTH);
        if (sound == null) {
            return;
        }
        sound.setLoop(true);
        SOUNDS.put(key, new TrackedSound(sound, payload));
        setPosition(sound, position);
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    private static void playTrack(SoundKey key, List<TrackData> tracks, int index, CompoundTag payload) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Vec3 position = currentPosition(level, key);
        if (position == null) {
            return;
        }
        while (index < tracks.size()) {
            TrackData track = tracks.get(index);
            SoundInstance sound = SoundTracker.getEtchedRecord(track.url(), track.getDisplayName(), level,
                    BlockPos.containing(position), 16, AudioSource.AudioFileType.FILE);
            if (sound == null) {
                index++;
                continue;
            }
            int nextIndex = index + 1;
            SoundInstance wrapped = StopListeningSound.create(sound,
                    () -> Minecraft.getInstance().tell(() -> advanceTrack(key, tracks, nextIndex, payload)));
            SOUNDS.put(key, new TrackedSound(wrapped, payload));
            setPosition(wrapped, position);
            Minecraft.getInstance().getSoundManager().play(wrapped);
            if (PlayableRecord.canShowMessage(position.x, position.y, position.z)) {
                Minecraft.getInstance().gui.setNowPlaying(track.getDisplayName());
            }
            return;
        }
    }

    private static void advanceTrack(SoundKey key, List<TrackData> tracks, int nextIndex, CompoundTag payload) {
        TrackedSound tracked = SOUNDS.get(key);
        if (tracked == null) {
            return;
        }
        if (nextIndex >= tracks.size()) {
            if (payload.contains("items", Tag.TAG_LIST)) {
                playTrack(key, tracks, 0, payload);
                return;
            }
            SOUNDS.remove(key);
            return;
        }
        playTrack(key, tracks, nextIndex, payload);
    }

    private static Vec3 currentPosition(ClientLevel level, SoundKey key) {
        if (level.getEntity(key.contraptionId()) instanceof AbstractContraptionEntity contraption && contraption.isAlive()) {
            return contraption.toGlobalVector(Vec3.atCenterOf(key.localPos()), 1);
        }
        return null;
    }

    private static void stopInstance(SoundInstance sound) {
        if (sound instanceof StopListeningSound stopListening) {
            stopListening.stopListening();
        }
        Minecraft.getInstance().getSoundManager().stop(sound);
    }

    private static Map<BlockPos, SoundInstance> blockSounds() {
        return ((LevelRendererAccessor) Minecraft.getInstance().levelRenderer).getPlayingJukeboxSongs();
    }

    private static void setPosition(SoundInstance sound, Vec3 position) {
        while (sound instanceof WrappedSoundInstance wrapped) {
            sound = wrapped.getParent();
        }
        if (sound instanceof AbstractSoundInstanceAccessor accessor) {
            accessor.atnf$setX(position.x);
            accessor.atnf$setY(position.y);
            accessor.atnf$setZ(position.z);
        }
    }
}
