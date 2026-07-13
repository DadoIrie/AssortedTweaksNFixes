package com.dadoirie.assortedtweaksnfixes.mixin.etched.jukeboxblockentitytracker;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.compat.etched.ContraptionPositionLogger;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(Contraption.class)
public abstract class ContraptionTrackerMixin {

    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(ContraptionTrackerMixin.class);

    @Shadow public BlockPos anchor;

    @Inject(method = "addBlock", at = @At("TAIL"))
    private void atf_captureLocalPosition(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        GlobalPos globalPos = GlobalPos.of(serverLevel.dimension(), pos);
        if (ContraptionPositionLogger.TRACKED_JUKEBOXES.remove(globalPos)) {
            BlockPos localPos = pos.subtract(this.anchor);
            ContraptionPositionLogger.ATNF$PENDING_LOCAL_POSITIONS
                    .computeIfAbsent((Contraption) (Object) this, k -> new HashMap<>())
                    .put(localPos, pos);
            LOGGER.info("CAPTURED jukebox → localPos: {} | global: {}", localPos, pos);
        }
    }

    @Inject(method = "onEntityCreated", at = @At("TAIL"))
    private void atf_startLoggingPosition(AbstractContraptionEntity entity, CallbackInfo ci) {
        if (entity instanceof CarriageContraptionEntity)
            return;

        Map<BlockPos, BlockPos> pendingLocalPositions =
                ContraptionPositionLogger.ATNF$PENDING_LOCAL_POSITIONS.remove((Contraption) (Object) this);
        if (pendingLocalPositions == null)
            return;

        for (Map.Entry<BlockPos, BlockPos> pending : pendingLocalPositions.entrySet()) {
            BlockPos localPos = pending.getKey();
            BlockPos originalPos = pending.getValue();

            ContraptionPositionLogger.ATNF$TRACKED.add(
                    new ContraptionPositionLogger.TrackedPosition(
                            entity,
                            localPos,
                            originalPos
                    )
            );

            LOGGER.info("ADDED normal contraption tracking - localPos: {}", localPos);
        }
    }
}