package com.dadoirie.assortedtweaksnfixes.compat.create.contraption;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import dev.corgitaco.dataanchor.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public record ContraptionBlockNbtS2C(int contraptionId, BlockPos localPos, CompoundTag nbt) implements Packet {

    public static final CustomPacketPayload.Type<ContraptionBlockNbtS2C> TYPE =
            new CustomPacketPayload.Type<>(ATNFConstants.identifer("contraption_block_nbt"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ContraptionBlockNbtS2C> STREAM_CODEC =
            CustomPacketPayload.codec(ContraptionBlockNbtS2C::write, ContraptionBlockNbtS2C::new);

    public ContraptionBlockNbtS2C(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readBlockPos(), buf.readNbt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(this.contraptionId);
        buf.writeBlockPos(this.localPos);
        buf.writeNbt(this.nbt);
    }

    @Override
    public void handle(@Nullable Level level, @Nullable Player player) {
        if (level == null || !level.isClientSide())
            return;

        if (!(level.getEntity(this.contraptionId) instanceof AbstractContraptionEntity contraptionEntity))
            return;

        Contraption contraption = contraptionEntity.getContraption();
        if (contraption == null)
            return;

        StructureBlockInfo info = contraption.getBlocks().get(this.localPos);
        if (info != null) {
            contraption.getBlocks().put(this.localPos, new StructureBlockInfo(this.localPos, info.state(), this.nbt));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
