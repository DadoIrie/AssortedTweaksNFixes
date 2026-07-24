package com.dadoirie.assortedtweaksnfixes.compat.jade;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.foundation.utility.RaycastHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.config.IWailaConfig;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class ContraptionBlockRaycast {

    private static final Cache<Entity, Optional<StructureBlockInfo>> CLIENT_CACHE = CacheBuilder.newBuilder()
            .weakKeys()
            .expireAfterWrite(50, TimeUnit.MILLISECONDS)
            .build();

    private ContraptionBlockRaycast() {
    }

    @OnlyIn(Dist.CLIENT)
    public static Optional<StructureBlockInfo> findTargetedBlock(EntityAccessor accessor, AbstractContraptionEntity contraptionEntity) {
        try {
            return CLIENT_CACHE.get(contraptionEntity, () -> raycastFromCamera(accessor, contraptionEntity));
        } catch (ExecutionException e) {
            return Optional.empty();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static Optional<StructureBlockInfo> raycastFromCamera(EntityAccessor accessor, AbstractContraptionEntity contraptionEntity) {
        Minecraft mc = Minecraft.getInstance();
        Entity camera = mc.getCameraEntity();
        Player player = accessor.getPlayer();
        if (camera == null || player == null)
            return Optional.empty();

        AttributeInstance reachAttribute = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reachAttribute == null)
            return Optional.empty();

        float partialTick = mc.getTimer().getGameTimeDeltaTicks();
        double reach = reachAttribute.getValue() + IWailaConfig.get().getGeneral().getExtendedReach();

        Optional<BlockPos> localPos = raycastPos(contraptionEntity,
                camera.getEyePosition(partialTick), camera.getViewVector(partialTick), reach, mc.level);
        return localPos.map(pos -> contraptionEntity.getContraption().getBlocks().get(pos));
    }

    private static Optional<BlockPos> raycastPos(AbstractContraptionEntity contraptionEntity, Vec3 eye,
                                                   Vec3 lookVector, double reach, Level level) {
        Contraption contraption = contraptionEntity.getContraption();
        if (contraption == null)
            return Optional.empty();

        Vec3 target = eye.add(lookVector.scale(reach));
        Vec3 localOrigin = contraptionEntity.toLocalVector(eye, 1);
        Vec3 localTarget = contraptionEntity.toLocalVector(target, 1);

        RaycastHelper.PredicateTraceResult result = RaycastHelper.rayTraceUntil(localOrigin, localTarget, pos -> {
            StructureBlockInfo info = contraption.getBlocks().get(pos);
            if (info == null)
                return false;

            VoxelShape shape = info.state().getShape(level, BlockPos.ZERO);
            if (shape.isEmpty())
                return false;

            return shape.clip(localOrigin, localTarget, pos) != null;
        });

        if (result == null || result.missed())
            return Optional.empty();

        return Optional.of(result.getPos());
    }
}
