package com.dadoirie.assortedtweaksnfixes.compat.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record ClientboundJukeboxPositionPacket(BlockPos originalPos, Vec3 pos) implements CustomPacketPayload {

    public static final Type<ClientboundJukeboxPositionPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AssortedTweaksNFixesConstants.MOD_ID, "jukebox_position"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundJukeboxPositionPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeBlockPos(packet.originalPos);
                buf.writeDouble(packet.pos.x);
                buf.writeDouble(packet.pos.y);
                buf.writeDouble(packet.pos.z);
            },
            buf -> new ClientboundJukeboxPositionPacket(
                    buf.readBlockPos(),
                    new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}