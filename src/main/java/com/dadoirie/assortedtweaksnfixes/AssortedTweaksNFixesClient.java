package com.dadoirie.assortedtweaksnfixes;

import com.dadoirie.assortedtweaksnfixes.client.KeybindRestorer;
import com.dadoirie.assortedtweaksnfixes.client.screen.BlastFurnaceTankScreen;
import com.dadoirie.assortedtweaksnfixes.client.screen.FurnaceTankScreen;
import com.dadoirie.assortedtweaksnfixes.client.screen.SmokerTankScreen;
import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.FurnaceTankRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = AssortedTweaksNFixesConstants.MOD_ID, dist = Dist.CLIENT)
public class AssortedTweaksNFixesClient {
    public AssortedTweaksNFixesClient(IEventBus modBus) {
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onRegisterMenuScreens);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        KeybindRestorer.getInstance().restore();
    }

    private void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(FurnaceTankRegistry.FURNACE_TANK_MENU.get(), FurnaceTankScreen::new);
        event.register(FurnaceTankRegistry.BLAST_FURNACE_TANK_MENU.get(), BlastFurnaceTankScreen::new);
        event.register(FurnaceTankRegistry.SMOKER_TANK_MENU.get(), SmokerTankScreen::new);
    }
}