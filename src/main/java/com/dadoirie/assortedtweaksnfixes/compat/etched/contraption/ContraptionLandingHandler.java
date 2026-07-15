package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.broadcast.PacketBroadcaster;
import gg.moonflower.etched.api.record.PlayableRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

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

        Packet packet = blockEntity instanceof JukeboxBlockEntity jukebox
                && PlayableRecord.isPlayableRecord(jukebox.getTheItem())
                ? new ContraptionRecordHandoffS2C(contraptionId, localPos, landingPos)
                : new ContraptionRecordStopS2C(contraptionId, localPos);

        PacketBroadcaster.S2C.trackingChunk(packet, level.getChunkAt(landingPos));
    }
}
