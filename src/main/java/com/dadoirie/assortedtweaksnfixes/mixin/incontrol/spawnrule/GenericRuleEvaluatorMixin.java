package com.dadoirie.assortedtweaksnfixes.mixin.incontrol.spawnrule;

import com.dadoirie.assortedtweaksnfixes.compat.incontrol.InControlRuleKeys;
import mcjty.incontrol.ForgeEventHandlers;
import mcjty.incontrol.rules.support.GenericRuleEvaluator;
import mcjty.incontrol.tools.rules.IEventQuery;
import mcjty.incontrol.tools.typed.AttributeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BiFunction;

@Mixin(GenericRuleEvaluator.class)
public abstract class GenericRuleEvaluatorMixin {

    @Unique
    private static final Logger atnf$LOGGER = LogManager.getLogger("incontrol");

    @Shadow
    @Final
    private List<BiFunction<Object, IEventQuery<Object>, Boolean>> checks;

    @Inject(method = "addChecks", at = @At("TAIL"))
    private void atf_consumeInhabitedThreshold(AttributeMap map, CallbackInfo callback) {
        map.consume(InControlRuleKeys.INHABITED_THRESHOLD, this::atnf$addInhabitedThresholdCheck);
    }

    @Unique
    private void atnf$addInhabitedThresholdCheck(int threshold) {
        final long thresholdTicks = threshold;
        this.checks.add((event, query) -> {
            LevelAccessor world = query.getWorld(event);
            BlockPos pos = query.getPos(event);
            LevelChunk chunk = world.getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
            if (chunk == null || !chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL)) {
                return false;
            }
            long inhabited = chunk.getInhabitedTime();
            boolean matched = inhabited >= thresholdTicks;
            atnf$debugLog(inhabited, thresholdTicks, matched);
            return matched;
        });
    }

    @Unique
    private static void atnf$debugLog(long inhabited, long threshold, boolean matched) {
        if (ForgeEventHandlers.debug) {
            atnf$LOGGER.info("inhabited: {}/{}, {}", inhabited, threshold, matched ? "DENY" : "PASS");
        }
    }
}