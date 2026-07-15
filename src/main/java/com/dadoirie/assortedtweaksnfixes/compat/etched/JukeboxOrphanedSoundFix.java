package com.dadoirie.assortedtweaksnfixes.compat.etched;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

// etched's client sounds are only stopped through its non-persistent "playing" flag, which is lost on
// chunk reload (and never set on a jukebox that landed from a contraption). Broadcasting the vanilla
// jukebox-stop level event (1011) is key-agnostic on the client and stops whatever sound sits at that
// position - vanilla or etched - so ejecting or breaking such a jukebox no longer orphans the audio.
public class JukeboxOrphanedSoundFix {

    private static final int JUKEBOX_STOP_EVENT = 1011;

    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getState().getBlock() instanceof JukeboxBlock))
            return;
        if (!(event.getLevel() instanceof ServerLevel level))
            return;

        level.levelEvent(JUKEBOX_STOP_EVENT, event.getPos(), 0);
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level))
            return;

        BlockState state = level.getBlockState(event.getPos());
        if (!(state.getBlock() instanceof JukeboxBlock)
                || !state.hasProperty(JukeboxBlock.HAS_RECORD)
                || !state.getValue(JukeboxBlock.HAS_RECORD))
            return;

        // vanilla skips block interaction while sneaking with an item in either hand - no eject happens then
        Player player = event.getEntity();
        boolean holdingAnything = !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty();
        if (player.isSecondaryUseActive() && holdingAnything)
            return;

        level.levelEvent(JUKEBOX_STOP_EVENT, event.getPos(), 0);
    }
}
