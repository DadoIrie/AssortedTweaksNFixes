package com.dadoirie.assortedtweaksnfixes.content;

import cn.mlus.thirst.foundation.common.capability.ModAttachment;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class HeatThirstHandler {

    private static final int INTERVAL = 40;
    private static final int RADIUS_H = 4;
    private static final int RADIUS_V = 2;
    private static final float EXHAUSTION_PER_PULSE = 0.3f;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide() || player.isCreative() || player.isSpectator())
            return;
        if ((player.tickCount + player.getId()) % INTERVAL != 0)
            return;

        if (isNearHeat(level, player.blockPosition()))
            player.getData(ModAttachment.PLAYER_THIRST).addExhaustion(player, EXHAUSTION_PER_PULSE);
    }

    private static boolean isNearHeat(Level level, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-RADIUS_H, -RADIUS_V, -RADIUS_H),
                center.offset(RADIUS_H, RADIUS_V, RADIUS_H))) {
            BlockState state = level.getBlockState(pos);
            if (state.getFluidState().is(FluidTags.LAVA))
                return true;
            if (state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.FIRE))
                return true;
            if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT))
                return true;
            if (state.getBlock() instanceof BlazeBurnerBlock
                    && state.getValue(BlazeBurnerBlock.HEAT_LEVEL).isAtLeast(HeatLevel.FADING))
                return true;
        }
        return false;
    }
}