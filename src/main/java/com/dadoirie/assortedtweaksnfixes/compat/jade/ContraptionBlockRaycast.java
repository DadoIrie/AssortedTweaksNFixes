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

// shared by every Jade provider that needs to know exactly which contraption block is under the crosshair.
// Jade's own entity hit-test only resolves to the contraption's outer bounding box, not a specific block, so
// this walks the real look ray through the contraption's local block grid instead, testing each candidate
// block's real VoxelShape as it's crossed (same technique Create's own Jade Addons integration uses).
public final class ContraptionBlockRaycast {

    // client-side lookups only: cached briefly so multiple providers processing the same hover within one
    // tooltip build reuse a single raycast instead of each repeating it
    private static final Cache<Entity, Optional<StructureBlockInfo>> CLIENT_CACHE = CacheBuilder.newBuilder()
            .weakKeys()
            .expireAfterWrite(50, TimeUnit.MILLISECONDS)
            .build();

    private ContraptionBlockRaycast() {
    }

    // for providers reading data the client already has locally (e.g. NBT that survived Create's client
    // sync unmodified) - relies on Minecraft.getInstance(), so it's client-only
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

    // for providers whose data is computed server-side (e.g. contraption item storage contents), where there's
    // no Minecraft client instance/camera to read from - the player's own eye position and look vector are
    // accurate enough here since they're driven by the normal movement/rotation sync, same as any other
    // server-side "what is this player looking at" check
    public static Optional<BlockPos> findTargetedBlockPos(Player player, AbstractContraptionEntity contraptionEntity) {
        if (player == null)
            return Optional.empty();

        AttributeInstance reachAttribute = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reachAttribute == null)
            return Optional.empty();

        return raycastPos(contraptionEntity, player.getEyePosition(1f), player.getViewVector(1f),
                reachAttribute.getValue(), contraptionEntity.level());
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
