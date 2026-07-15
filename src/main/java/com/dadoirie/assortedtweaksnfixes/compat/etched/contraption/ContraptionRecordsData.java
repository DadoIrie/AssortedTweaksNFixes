package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.client.ContraptionSoundManager;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.corgitaco.dataanchor.data.SyncedTrackedData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import dev.corgitaco.dataanchor.data.type.entity.SyncedEntityTrackedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// derived view over the contraption's blocks map: synced into the entity's spawn bundle on pairing
// (walk into range, rejoin, assembly) and re-broadcast on interaction - never stored, never persisted
public final class ContraptionRecordsData extends SyncedEntityTrackedData {

    public static final TrackedDataKey<ContraptionRecordsData> KEY = TrackedDataRegistries.ENTITY.register(
            ATNFConstants.identifer("contraption_records"),
            ContraptionRecordsData.class,
            (key, entity) -> entity instanceof AbstractContraptionEntity
                    ? new ContraptionRecordsData(key, entity)
                    : null);

    public ContraptionRecordsData(TrackedDataKey<ContraptionRecordsData> trackedDataKey, Entity entity) {
        super(trackedDataKey, entity);
    }

    public static void init() {
    }

    public static void sync(AbstractContraptionEntity contraptionEntity) {
        TrackedDataRegistries.ENTITY.get(KEY, contraptionEntity).ifPresent(SyncedTrackedData::sync);
    }

    @Override
    public CompoundTag writeToNetwork() {
        CompoundTag tag = new CompoundTag();
        ListTag entries = new ListTag();
        if (this.entity instanceof AbstractContraptionEntity contraptionEntity) {
            Contraption contraption = contraptionEntity.getContraption();
            if (contraption != null) {
                for (MutablePair<StructureBlockInfo, MovementContext> actor : contraption.getActors()) {
                    BlockPos localPos = actor.left.pos();
                    StructureBlockInfo info = contraption.getBlocks().get(localPos);
                    if (info == null
                            || !(MovementBehaviour.REGISTRY.get(info.state()) instanceof RecordPlayerMovementBehaviour behaviour))
                        continue;

                    CompoundTag payload = behaviour.writeSyncData(info, this.entity.registryAccess());
                    if (payload == null)
                        continue;

                    payload.putLong("pos", localPos.asLong());
                    payload.putLong("original", localPos.offset(contraption.anchor).asLong());
                    entries.add(payload);
                }
            }
        }
        tag.put("entries", entries);
        return tag;
    }

    @Override
    public void readFromNetwork(CompoundTag tag) {
        if (!this.entity.level().isClientSide())
            return;

        List<ContraptionSoundManager.RecordEntry> entries = new ArrayList<>();
        for (Tag element : tag.getList("entries", Tag.TAG_COMPOUND)) {
            CompoundTag payload = (CompoundTag) element;
            entries.add(new ContraptionSoundManager.RecordEntry(
                    BlockPos.of(payload.getLong("pos")),
                    BlockPos.of(payload.getLong("original")),
                    payload));
        }
        ContraptionSoundManager.reconcile(this.entity, entries);
    }

    @Override
    @Nullable
    public CompoundTag save() {
        return null;
    }

    @Override
    public void load(CompoundTag tag) {
    }
}
