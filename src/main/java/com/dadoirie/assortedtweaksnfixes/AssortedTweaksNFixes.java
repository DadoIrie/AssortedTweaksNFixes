package com.dadoirie.assortedtweaksnfixes;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import com.dadoirie.assortedtweaksnfixes.compat.yigd.DeathCharmCompat;
import com.dadoirie.assortedtweaksnfixes.mixin.ConditionalMixinPlugin;

@Mod(AssortedTweaksNFixesConstants.MOD_ID)
public class AssortedTweaksNFixes {
    private static final boolean IS_DEDICATED_SERVER = FMLEnvironment.dist.isDedicatedServer();

    public AssortedTweaksNFixes() {
        AssortedTweaksNFixesConstants.LOGGER.info("AssortedTweaksNFixes loaded on {}", IS_DEDICATED_SERVER ? "dedicated server" : "client");

        if (ConditionalMixinPlugin.isMixinEnabled("deathcharm.RestoreInventoryEventsMixin")) {
            DeathCharmCompat.init();
            AssortedTweaksNFixesConstants.LOGGER.info("DeathCharmCompat initialized because mixin 'RestoreInventoryEventsMixin' is enabled");
        }
    }
}