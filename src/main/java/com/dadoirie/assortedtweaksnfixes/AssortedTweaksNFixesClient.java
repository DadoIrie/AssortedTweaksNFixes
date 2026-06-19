package com.dadoirie.assortedtweaksnfixes;

import com.dadoirie.assortedtweaksnfixes.client.Freecam;
import com.dadoirie.assortedtweaksnfixes.client.KeybindHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
@Mod(value = AssortedTweaksNFixesConstants.MOD_ID, dist = Dist.CLIENT)
public class AssortedTweaksNFixesClient {
    public AssortedTweaksNFixesClient(IEventBus modBus) {
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onRegisterKeyMappings);
    }
    private void onClientSetup(FMLClientSetupEvent event) {
        KeybindHandler handler = KeybindHandler.getInstance();
        handler.init();
        handler.restore();
    }
    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(Freecam.TOGGLE_KEY);
    }
}