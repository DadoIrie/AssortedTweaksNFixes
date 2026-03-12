package com.dadoirie.assortedtweaksnfixes;

import com.dadoirie.assortedtweaksnfixes.compat.mekanism.DyeDepotCompat;
import net.neoforged.bus.api.IEventBus;
import org.slf4j.Logger;
import com.dadoirie.assortedtweaksnfixes.compat.yigd.DeathCharmCompat;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(AssortedTweaksNFixesConstants.MOD_ID)
public class AssortedTweaksNFixes {
    private static final boolean IS_DEDICATED_SERVER = FMLEnvironment.dist.isDedicatedServer();
    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(AssortedTweaksNFixes.class);

    public AssortedTweaksNFixes(IEventBus modEventBus) {
        LOGGER.info("AssortedTweaksNFixes loaded on {}", IS_DEDICATED_SERVER ? "dedicated server" : "client");
        if (ModList.get().isLoaded("mekanism") && ModList.get().isLoaded("dye_depot")) {
            if (!ModList.get().isLoaded("recipe_modification")) {
                throw new IllegalStateException("Recipe modification mod is required for the Mekanism and Dye Depot compat.");
            }
            DyeDepotCompat.register(modEventBus);
        }
        DeathCharmCompat.init();
    }
}