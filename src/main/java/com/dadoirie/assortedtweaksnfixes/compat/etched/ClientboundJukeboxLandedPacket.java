package com.dadoirie.assortedtweaksnfixes.compat.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientboundJukeboxLandedPacket(BlockPos originalPos, BlockPos landingPos) implements CustomPacketPayload {

    public static final Type<ClientboundJukeboxLandedPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AssortedTweaksNFixesConstants.MOD_ID, "jukebox_landed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundJukeboxLandedPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeBlockPos(packet.originalPos);
                buf.writeBlockPos(packet.landingPos);
            },
            buf -> new ClientboundJukeboxLandedPacket(
                    buf.readBlockPos(),
                    buf.readBlockPos()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}