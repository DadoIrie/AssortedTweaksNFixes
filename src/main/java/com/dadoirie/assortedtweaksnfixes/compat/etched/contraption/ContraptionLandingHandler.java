package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class ContraptionLandingHandler {

    private static final String STAMP_KEY = "atnf_contraption_id";
    private static final String FORGE_DATA_KEY = "NeoForgeData";

    static void stamp(CompoundTag nbt, AbstractContraptionEntity entity) {
        if (nbt == null || entity == null)
            return;

        CompoundTag forgeData = nbt.getCompound(FORGE_DATA_KEY);
        forgeData.putInt(STAMP_KEY, entity.getId());
        nbt.put(FORGE_DATA_KEY, forgeData);
    }

    private static int clearStamp(BlockEntity blockEntity) {
        CompoundTag data = blockEntity.getPersistentData();
        if (!data.contains(STAMP_KEY))
            return -1;

        int contraptionId = data.getInt(STAMP_KEY);
        data.remove(STAMP_KEY);
        blockEntity.setChanged();
        return contraptionId;
    }

    public static void onLanded(BlockEntity blockEntity, StructureTransform transform) {
        if (!(blockEntity.getLevel() instanceof ServerLevel level))
            return;

        int contraptionId = clearStamp(blockEntity);
        if (contraptionId < 0)
            return;

        BlockPos landingPos = blockEntity.getBlockPos();
        BlockPos localPos = transform.unapply(landingPos);

        Packet packet = isStillPlayable(blockEntity, level)
                ? new ContraptionRecordHandoffS2C(contraptionId, localPos, landingPos)
                : new ContraptionRecordStopS2C(contraptionId, localPos);

        PacketBroadcaster.S2C.trackingChunk(packet, level.getChunkAt(landingPos));
    }

    private static boolean isStillPlayable(BlockEntity blockEntity, ServerLevel level) {
        BlockState state = blockEntity.getBlockState();
        if (!(MovementBehaviour.REGISTRY.get(state) instanceof RecordPlayerMovementBehaviour behaviour))
            return false;

        StructureBlockInfo info = new StructureBlockInfo(blockEntity.getBlockPos(), state,
                blockEntity.saveWithoutMetadata(level.registryAccess()));
        return behaviour.writeSyncData(info, level.registryAccess()) != null;
    }
}