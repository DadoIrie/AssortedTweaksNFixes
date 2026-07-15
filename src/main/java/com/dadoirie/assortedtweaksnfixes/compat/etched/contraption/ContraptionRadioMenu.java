package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import gg.moonflower.etched.common.menu.RadioMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;

public class ContraptionRadioMenu extends RadioMenu {

    private final AbstractContraptionEntity contraptionEntity;
    private final BlockPos localPos;

    public ContraptionRadioMenu(int id, AbstractContraptionEntity contraptionEntity, BlockPos localPos) {
        super(id, ContainerLevelAccess.NULL);
        this.contraptionEntity = contraptionEntity;
        this.localPos = localPos;
    }

    @Override
    public boolean stillValid(Player player) {
        if (!this.contraptionEntity.isAlive())
            return false;

        StructureBlockInfo info = this.contraptionEntity.getContraption().getBlocks().get(this.localPos);
        if (info == null || !(info.state().getBlock() instanceof RadioBlock))
            return false;

        Vec3 position = this.contraptionEntity.toGlobalVector(Vec3.atCenterOf(this.localPos), 1);
        return player.distanceToSqr(position) <= 64.0;
    }

    @Override
    public void setUrl(String url) {
        RecordPlayerInteractionBehaviour.withBlockEntity(this.contraptionEntity, this.localPos,
                RadioBlockEntity::new, blockEntity -> blockEntity.setUrl(url));
        ContraptionRecordsData.sync(this.contraptionEntity);
    }
}
