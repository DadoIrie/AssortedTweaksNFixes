package com.dadoirie.assortedtweaksnfixes.mixin.cablefacades;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.portingdeadmods.cable_facades.CFConfig;
import com.portingdeadmods.cable_facades.utils.FacadeUtils;
import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(VisualizationHelper.class)
public abstract class CoveredVisualizerMixin {

    @Unique
    private static final Map<BlockEntity, Boolean> atnf$lastCovered = new WeakHashMap<>();

    @ModifyReturnValue(
            method = "getVisualizer(Lnet/minecraft/world/level/block/entity/BlockEntity;)"
                    + "Ldev/engine_room/flywheel/api/visualization/BlockEntityVisualizer;",
            at = @At("RETURN")
    )
    private static BlockEntityVisualizer<?> atnf$removeWhenFacaded(
            BlockEntityVisualizer<?> original, BlockEntity blockEntity) {

        boolean covered = atnf$isCovered(blockEntity);

        Boolean last = atnf$lastCovered.put(blockEntity, covered);
        boolean transitioned = (last == null) ? covered : (last != covered);
        if (transitioned) {
            atnf$forceRebuild(blockEntity);
        }

        if (covered) {
            VisualizationManager manager = VisualizationManager.get(blockEntity.getLevel());
            if (manager != null) {
                manager.blockEntities().queueRemove(blockEntity);
            }
            return null;
        }
        return original;
    }

    @Unique
    private static boolean atnf$isCovered(BlockEntity be) {
        Level level = be.getLevel();
        if (level == null) {
            return false;
        }
        BlockPos pos = be.getBlockPos();
        return FacadeUtils.hasFacade(level, pos)
                && CFConfig.shouldHideWhenFacaded(be.getBlockState().getBlock());
    }

    @Unique
    private static void atnf$forceRebuild(BlockEntity be) {
        if (be.getLevel() instanceof ClientLevel clientLevel) {
            Minecraft.getInstance().execute(() -> {
                be.requestModelDataUpdate();
                BlockPos pos = be.getBlockPos();
                clientLevel.setSectionDirtyWithNeighbors(
                        SectionPos.blockToSectionCoord(pos.getX()),
                        SectionPos.blockToSectionCoord(pos.getY()),
                        SectionPos.blockToSectionCoord(pos.getZ())
                );
            });
        }
    }
}