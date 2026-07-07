package com.dadoirie.assortedtweaksnfixes.client;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = AssortedTweaksNFixesConstants.MOD_ID, value = Dist.CLIENT)
public class AccessoriesMountHandler {

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (!ModList.get().isLoaded("accessories")) return;
        if (!SafeCheck.isAccessoriesScreen(event.getScreen())) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        if (player.getVehicle() instanceof HasCustomInventoryScreen) {
            event.setCanceled(true);
            player.sendOpenInventory();
        }
    }

    private static class SafeCheck {
        private static boolean isAccessoriesScreen(Screen screen) {
            return screen instanceof AccessoriesExperimentalScreen;
        }
    }
}