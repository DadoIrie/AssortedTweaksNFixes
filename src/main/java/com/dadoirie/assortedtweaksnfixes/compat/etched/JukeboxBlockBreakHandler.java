package com.dadoirie.assortedtweaksnfixes.compat.etched;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.JukeboxBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class JukeboxBlockBreakHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getState().getBlock() instanceof JukeboxBlock))
            return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;

        BlockPos pos = event.getPos();
        PacketDistributor.sendToPlayersNear(serverLevel, null, pos.getX(), pos.getY(), pos.getZ(), 64,
                new ClientboundJukeboxStopPacket(pos));
    }
}