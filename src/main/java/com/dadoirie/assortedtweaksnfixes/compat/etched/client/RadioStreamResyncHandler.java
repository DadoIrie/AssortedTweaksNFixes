package com.dadoirie.assortedtweaksnfixes.compat.etched.client;

import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.client.ContraptionSoundManager;
import com.dadoirie.assortedtweaksnfixes.mixin.ConditionalMixinPlugin;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.List;
import java.util.Map;

// while the pause screen is up on a session that is actually paused (singleplayer, not LAN-shared), the
// integrated server thread stops but each radio's network stream keeps buffering in the background - closing
// the screen can leave playback stale or desynced, so force every currently playing radio to reconnect
public final class RadioStreamResyncHandler {

    private RadioStreamResyncHandler() {
    }

    public static void onScreenClosing(ScreenEvent.Closing event) {
        if (!(event.getScreen() instanceof PauseScreen))
            return;

        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null || server.isPublished())
            return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return;

        reconnectStationaryRadios(level);

        if (ModList.get().isLoaded("create")
                && ConditionalMixinPlugin.isApplied("etched.RadioBlockMixin")
                && ConditionalMixinPlugin.isApplied("etched.SoundTrackerMixin")) {
            ContraptionSoundManager.reconnectRadios(level);
        }
    }

    private static void reconnectStationaryRadios(ClientLevel level) {
        Map<BlockPos, SoundInstance> blockSounds = SoundContinuity.blockSounds();
        for (BlockPos pos : List.copyOf(blockSounds.keySet())) {
            if (!(level.getBlockEntity(pos) instanceof RadioBlockEntity radio))
                continue;

            SoundInstance sound = blockSounds.remove(pos);
            if (sound == null)
                continue;

            SoundContinuity.stopInstance(sound);
            SoundTracker.playRadio(radio.getUrl(), radio.getBlockState(), level, pos);
        }
    }
}
