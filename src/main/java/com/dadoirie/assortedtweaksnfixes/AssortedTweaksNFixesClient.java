package com.dadoirie.assortedtweaksnfixes;

import com.dadoirie.assortedtweaksnfixes.client.KeybindRestorer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = AssortedTweaksNFixesConstants.MOD_ID, dist = Dist.CLIENT)
public class AssortedTweaksNFixesClient {

    public AssortedTweaksNFixesClient(IEventBus modBus) {
        modBus.addListener(this::onClientSetup);

    }

    private void onClientSetup(FMLClientSetupEvent event) {
        KeybindRestorer.getInstance().restore();
    }
}