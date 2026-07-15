package com.dadoirie.assortedtweaksnfixes.compat.incontrol;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.chunk.ServerLevelChunkTrackedData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SpeciesClaimChunkData extends ServerLevelChunkTrackedData {

    public static ModConfigSpec.IntValue CHUNK_RESERVATION_VERIFY_TICKS;

    public static final TrackedDataKey<SpeciesClaimChunkData> KEY = TrackedDataRegistries.CHUNK.register(
            ATNFConstants.identifer("species_claim"),
            SpeciesClaimChunkData.class,
            (key, chunk) -> chunk instanceof LevelChunk levelChunk
                    ? new SpeciesClaimChunkData(key, levelChunk)
                    : null);

    public interface ConditionsFlag {

        boolean atnf$chunkReservation();
    }

    public interface BuilderFlag {

        void atnf$setChunkReservation(boolean value);

        boolean atnf$getChunkReservation();
    }

    @Nullable
    private String claimedSpecies;
    private long lastVerified;

    public SpeciesClaimChunkData(TrackedDataKey<SpeciesClaimChunkData> trackedDataKey, LevelChunk chunk) {
        super(trackedDataKey, chunk);
    }

    public static void init() {
    }

    @Nullable
    public static SpeciesClaimChunkData get(LevelChunk chunk) {
        return TrackedDataRegistries.CHUNK.get(KEY, chunk).orElse(null);
    }

    @Nullable
    public String claimedSpecies() {
        return claimedSpecies;
    }

    public boolean allows(ServerLevel level, LevelChunk chunk, EntityType<?> species) {
        if (claimedSpecies == null) {
            return true;
        }
        if (claimedSpecies.equals(BuiltInRegistries.ENTITY_TYPE.getKey(species).toString())) {
            return true;
        }
        long now = level.getGameTime();
        if (now - lastVerified < CHUNK_RESERVATION_VERIFY_TICKS.get()) {
            return false;
        }
        lastVerified = now;
        markDirty();
        if (claimantPresent(level, chunk, claimedSpecies)) {
            return false;
        }
        claimedSpecies = null;
        markDirty();
        return true;
    }

    public void claim(ServerLevel level, EntityType<?> species) {
        if (claimedSpecies == null) {
            claimedSpecies = BuiltInRegistries.ENTITY_TYPE.getKey(species).toString();
            lastVerified = level.getGameTime();
            markDirty();
        }
    }

    private static boolean claimantPresent(ServerLevel level, LevelChunk chunk, String claimedId) {
        Optional<EntityType<?>> type = BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.parse(claimedId));
        return type.isPresent() && anyAlive(level, chunk, type.get());
    }

    private static <T extends Entity> boolean anyAlive(ServerLevel level, LevelChunk chunk, EntityType<T> type) {
        ChunkPos pos = chunk.getPos();
        AABB box = new AABB(
                pos.getMinBlockX(), level.getMinBuildHeight(), pos.getMinBlockZ(),
                pos.getMaxBlockX() + 1, level.getMaxBuildHeight(), pos.getMaxBlockZ() + 1);
        List<T> found = new ArrayList<>(1);
        level.getEntities(type, box, Entity::isAlive, found, 1);
        return !found.isEmpty();
    }

    @Override
    @Nullable
    public CompoundTag save() {
        if (claimedSpecies == null) {
            return null;
        }
        CompoundTag tag = new CompoundTag();
        tag.putString("species", claimedSpecies);
        tag.putLong("lastVerified", lastVerified);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        claimedSpecies = tag.contains("species") ? tag.getString("species") : null;
        lastVerified = tag.getLong("lastVerified");
    }
}