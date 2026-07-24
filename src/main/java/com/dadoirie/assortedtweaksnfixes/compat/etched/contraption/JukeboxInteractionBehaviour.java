package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.dadoirie.assortedtweaksnfixes.compat.create.contraption.ATNFInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.api.record.PlayableRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class JukeboxInteractionBehaviour extends ATNFInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
                                           AbstractContraptionEntity contraptionEntity) {
        if (player.level().isClientSide())
            return true;

        StructureBlockInfo info = contraptionEntity.getContraption().getBlocks().get(localPos);
        if (info == null || !info.state().hasProperty(JukeboxBlock.HAS_RECORD))
            return false;

        if (info.state().getValue(JukeboxBlock.HAS_RECORD)) {
            withBlockEntity(contraptionEntity, localPos, JukeboxBlockEntity::new, JukeboxBlockEntity::popOutTheItem);
            ContraptionRecordsData.sync(contraptionEntity);
            return true;
        }

        ItemStack held = player.getItemInHand(activeHand);
        if (!PlayableRecord.isPlayableRecord(held))
            return false;

        ItemStack record = held.consumeAndReturn(1, player);
        withBlockEntity(contraptionEntity, localPos, JukeboxBlockEntity::new, blockEntity -> blockEntity.setTheItem(record));
        player.awardStat(Stats.PLAY_RECORD);
        ContraptionRecordsData.sync(contraptionEntity);
        return true;
    }
}
