package com.dadoirie.assortedtweaksnfixes.compat.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientboundJukeboxStopPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<ClientboundJukeboxStopPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AssortedTweaksNFixesConstants.MOD_ID, "jukebox_stop"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundJukeboxStopPacket> CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeBlockPos(packet.pos),
            buf -> new ClientboundJukeboxStopPacket(buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}