package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.common.block.AlbumJukeboxBlock;
import gg.moonflower.etched.core.Etched;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class AlbumJukeboxInteractionBehaviour extends RecordPlayerInteractionBehaviour {

    private static final Component CONTAINER_TITLE = Component.translatable("container." + Etched.MOD_ID + ".album_jukebox");

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
                                           AbstractContraptionEntity contraptionEntity) {
        if (player.level().isClientSide())
            return true;

        StructureBlockInfo info = contraptionEntity.getContraption().getBlocks().get(localPos);
        if (info == null || !(info.state().getBlock() instanceof AlbumJukeboxBlock))
            return false;

        player.openMenu(new SimpleMenuProvider(
                        (id, inventory, menuPlayer) -> ContraptionAlbumJukeboxMenu.create(id, inventory, contraptionEntity, localPos),
                        CONTAINER_TITLE),
                buf -> buf.writeBlockPos(localPos));
        return true;
    }
}
