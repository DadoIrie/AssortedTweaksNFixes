package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.dadoirie.assortedtweaksnfixes.compat.create.contraption.ATNFInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.common.block.AlbumJukeboxBlock;
import gg.moonflower.etched.common.blockentity.AlbumJukeboxBlockEntity;
import gg.moonflower.etched.common.menu.AlbumJukeboxMenu;
import gg.moonflower.etched.common.network.play.SetAlbumJukeboxTrackPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class ContraptionAlbumJukeboxMenu extends AlbumJukeboxMenu {

    private final AbstractContraptionEntity contraptionEntity;
    private final BlockPos localPos;
    private final AlbumJukeboxBlockEntity blockEntity;
    private final AtomicReference<BlockState> state;

    private ContraptionAlbumJukeboxMenu(int id, Inventory inventory, AlbumJukeboxBlockEntity blockEntity,
                                        AtomicReference<BlockState> state,
                                        AbstractContraptionEntity contraptionEntity, BlockPos localPos) {
        super(id, inventory, blockEntity, localPos);
        this.contraptionEntity = contraptionEntity;
        this.localPos = localPos;
        this.blockEntity = blockEntity;
        this.state = state;
    }

    @Nullable
    public static ContraptionAlbumJukeboxMenu create(int id, Inventory inventory,
                                                     AbstractContraptionEntity contraptionEntity, BlockPos localPos) {
        AtomicReference<BlockState> state = new AtomicReference<>();
        AlbumJukeboxBlockEntity blockEntity = ATNFInteractionBehaviour.createBlockEntity(
                contraptionEntity, localPos, AlbumJukeboxBlockEntity::new, state);
        if (blockEntity == null)
            return null;

        return new ContraptionAlbumJukeboxMenu(id, inventory, blockEntity, state, contraptionEntity, localPos);
    }

    @Override
    public boolean stillValid(Player player) {
        if (!this.contraptionEntity.isAlive())
            return false;

        StructureBlockInfo info = this.contraptionEntity.getContraption().getBlocks().get(this.localPos);
        if (info == null || !(info.state().getBlock() instanceof AlbumJukeboxBlock))
            return false;

        Vec3 position = this.contraptionEntity.toGlobalVector(Vec3.atCenterOf(this.localPos), 1);
        return player.distanceToSqr(position) <= 64.0;
    }

    @Override
    public boolean setPlayingTrack(Level level, SetAlbumJukeboxTrackPacket pkt) {
        return false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player.level().isClientSide() || !this.contraptionEntity.isAlive())
            return;

        ATNFInteractionBehaviour.writeBack(this.contraptionEntity, this.localPos, this.state.get(), this.blockEntity);
        ContraptionRecordsData.sync(this.contraptionEntity);
    }
}
