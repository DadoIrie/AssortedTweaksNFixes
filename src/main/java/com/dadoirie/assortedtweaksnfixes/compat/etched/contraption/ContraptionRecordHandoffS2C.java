package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.client.ContraptionSoundManager;
import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record ContraptionRecordHandoffS2C(int contraptionId, BlockPos localPos, BlockPos landingPos) implements Packet {

    public static final CustomPacketPayload.Type<ContraptionRecordHandoffS2C> TYPE =
            new CustomPacketPayload.Type<>(ATNFConstants.identifer("contraption_record_handoff"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ContraptionRecordHandoffS2C> STREAM_CODEC =
            CustomPacketPayload.codec(ContraptionRecordHandoffS2C::write, ContraptionRecordHandoffS2C::new);

    public ContraptionRecordHandoffS2C(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readBlockPos(), buf.readBlockPos());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.contraptionId);
        buf.writeBlockPos(this.localPos);
        buf.writeBlockPos(this.landingPos);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (level != null && level.isClientSide()) {
            ContraptionSoundManager.handoff(this.contraptionId, this.localPos, this.landingPos);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
