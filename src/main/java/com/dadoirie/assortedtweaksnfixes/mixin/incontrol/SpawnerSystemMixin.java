package com.dadoirie.assortedtweaksnfixes.mixin.incontrol;

import com.dadoirie.assortedtweaksnfixes.compat.incontrol.SpeciesClaimChunkData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mcjty.incontrol.ForgeEventHandlers;
import mcjty.incontrol.spawner.SpawnerConditions;
import mcjty.incontrol.spawner.SpawnerSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpawnerSystem.class)
public abstract class SpawnerSystemMixin {

    @Unique
    private static final Logger atnf$LOGGER = LogManager.getLogger("incontrol");

    @Unique
    private static boolean atnf$reservationActive;

    @WrapOperation(
            method = "executeRule(ILmcjty/incontrol/spawner/SpawnerRule;Lnet/minecraft/server/level/ServerLevel;Lmcjty/incontrol/mob/DefaultMob;Lnet/minecraft/world/entity/MobCategory;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lmcjty/incontrol/spawner/SpawnerSystem;getRandomPosition(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntityType;Lmcjty/incontrol/spawner/SpawnerConditions;Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/core/BlockPos;"
            )
    )
    private static BlockPos atnf$gateReservedChunk(Level world, EntityType<?> mob, SpawnerConditions conditions,
                                                  BlockPos groupCenterPos, int groupDistance, Operation<BlockPos> original) {
        atnf$reservationActive = ((SpeciesClaimChunkData.ConditionsFlag) conditions).atnf$chunkReservation();
        BlockPos pos = original.call(world, mob, conditions, groupCenterPos, groupDistance);
        if (atnf$reservationActive && pos != null && mob != null && world instanceof ServerLevel serverLevel) {
            LevelChunk chunk = serverLevel.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
            SpeciesClaimChunkData claim = chunk != null ? SpeciesClaimChunkData.get(chunk) : null;
            if (claim != null) {
                String claimedBefore = claim.claimedSpecies();
                boolean allowed = claim.allows(serverLevel, chunk, mob);
                atnf$debugLog(chunk, claimedBefore, mob, allowed);
                return allowed ? pos : null;
            }
        }
        return pos;
    }

    @Unique
    private static void atnf$debugLog(LevelChunk chunk, String claimedBefore, EntityType<?> mob, boolean allowed) {
        if (ForgeEventHandlers.debug) {
            atnf$LOGGER.info("Reservation {} claimed: {} mob: {} -> {}",
                    chunk.getPos(),
                    claimedBefore != null ? "true (" + claimedBefore + ")" : "false",
                    BuiltInRegistries.ENTITY_TYPE.getKey(mob),
                    allowed ? "PASS" : "DENY");
        }
    }

    @WrapOperation(
            method = "executeRule(ILmcjty/incontrol/spawner/SpawnerRule;Lnet/minecraft/server/level/ServerLevel;Lmcjty/incontrol/mob/DefaultMob;Lnet/minecraft/world/entity/MobCategory;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntityWithPassengers(Lnet/minecraft/world/entity/Entity;)V"
            )
    )
    private static void atnf$claimChunkOnSpawn(ServerLevel world, Entity entity, Operation<Void> original) {
        original.call(world, entity);
        if (atnf$reservationActive) {
            BlockPos pos = entity.blockPosition();
            LevelChunk chunk = world.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
            SpeciesClaimChunkData claim = chunk != null ? SpeciesClaimChunkData.get(chunk) : null;
            if (claim != null) {
                claim.claim(world, entity.getType());
            }
        }
    }
}