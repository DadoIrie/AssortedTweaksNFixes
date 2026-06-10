package com.dadoirie.assortedtweaksnfixes;

import com.dadoirie.assortedtweaksnfixes.client.KeybindRestorer;
import com.dadoirie.assortedtweaksnfixes.registry.createfurnacelavaadapter.client.ColoredAdapterClientSetup;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = AssortedTweaksNFixesConstants.MOD_ID, dist = Dist.CLIENT)
public class AssortedTweaksNFixesClient {

    public AssortedTweaksNFixesClient(IEventBus modBus) {
        modBus.addListener(this::onClientSetup);

        if (ModList.get().isLoaded("create_furnace_lava_adapter") &&
                ModList.get().isLoaded("colorfulpipes")) {

            ColoredAdapterClientSetup.init(modBus);
        }
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        KeybindRestorer.getInstance().restore();
    }
}