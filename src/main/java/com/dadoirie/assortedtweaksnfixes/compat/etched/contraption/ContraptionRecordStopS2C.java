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

public record ContraptionRecordStopS2C(int contraptionId, BlockPos localPos) implements Packet {

    public static final CustomPacketPayload.Type<ContraptionRecordStopS2C> TYPE =
            new CustomPacketPayload.Type<>(ATNFConstants.identifer("contraption_record_stop"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ContraptionRecordStopS2C> STREAM_CODEC =
            CustomPacketPayload.codec(ContraptionRecordStopS2C::write, ContraptionRecordStopS2C::new);

    public ContraptionRecordStopS2C(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readBlockPos());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.contraptionId);
        buf.writeBlockPos(this.localPos);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (level != null && level.isClientSide()) {
            ContraptionSoundManager.stop(this.contraptionId, this.localPos);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
