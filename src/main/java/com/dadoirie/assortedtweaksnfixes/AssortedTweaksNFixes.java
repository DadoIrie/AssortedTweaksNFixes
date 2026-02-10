package com.dadoirie.assortedtweaksnfixes;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

// TODO: Fix scroll working in the experimental menu due to mousetweaks
// TODO: Move apothic attibutes button from vanilla menu to experiemental (Neo only)
@Mod(AssortedTweaksNFixesConstants.MOD_ID)
public class AssortedTweaksNFixes {
    private static final boolean IS_DEDICATED_SERVER = FMLEnvironment.dist.isDedicatedServer();

    public AssortedTweaksNFixes() {
        AssortedTweaksNFixesConstants.LOGGER.info("AssortedTweaksNFixes loaded on {}", IS_DEDICATED_SERVER ? "dedicated server" : "client");
    }
}
