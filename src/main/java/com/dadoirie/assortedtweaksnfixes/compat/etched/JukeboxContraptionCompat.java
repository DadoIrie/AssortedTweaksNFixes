package com.dadoirie.assortedtweaksnfixes.compat.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.transformable.MovedBlockTransformerRegistries;
import gg.moonflower.etched.api.record.PlayableRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

public class JukeboxContraptionCompat {

    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(JukeboxContraptionCompat.class);

    public static void register() {
        MovementBehaviour.REGISTRY.register(Blocks.JUKEBOX, new JukeboxMovementBehaviour());

        MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.register(BlockEntityType.JUKEBOX, (be, transform) -> {
            if (!(be instanceof JukeboxBlockEntity jukebox))
                return;
            if (!PlayableRecord.isPlayableRecord(jukebox.getTheItem()))
                return;
            if (!(be.getLevel() instanceof ServerLevel serverLevel))
                return;

            BlockPos landingPos = be.getBlockPos();
            BlockPos localPos = transform.unapply(landingPos);
            BlockPos originalPos = JukeboxMovementBehaviour.PENDING_ORIGINAL_POS.remove(localPos);
            if (originalPos == null)
                return;

            PacketDistributor.sendToPlayersNear(serverLevel, null,

                    landingPos.getX(), landingPos.getY(), landingPos.getZ(), 64,
                    new ClientboundJukeboxLandedPacket(originalPos, landingPos));
        });
    }
}
