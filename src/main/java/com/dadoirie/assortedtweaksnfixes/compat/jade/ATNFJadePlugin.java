package com.dadoirie.assortedtweaksnfixes.compat.jade;

import com.dadoirie.assortedtweaksnfixes.compat.jade.create.ContraptionCustomNameJadeSupport;
import com.dadoirie.assortedtweaksnfixes.compat.jade.create.ContraptionHoveredStorageJadeSupport;
import com.dadoirie.assortedtweaksnfixes.compat.jade.etched.EtchedContraptionJadeSupport;
import com.dadoirie.assortedtweaksnfixes.compat.jade.etched.EtchedJadeSupport;
import net.neoforged.fml.ModList;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@SuppressWarnings("unused")
@WailaPlugin
public class ATNFJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        if (ModList.get().isLoaded("create")) {
            ContraptionHoveredStorageJadeSupport.register(registration);
        }
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        if (ModList.get().isLoaded("etched")) {
            EtchedJadeSupport.registerClient(registration);
        }
        if (ModList.get().isLoaded("create")) {
            ContraptionCustomNameJadeSupport.registerClient(registration);
            ContraptionHoveredStorageJadeSupport.registerClient(registration);
        }
        if (ModList.get().isLoaded("create") && ModList.get().isLoaded("etched")) {
            EtchedContraptionJadeSupport.registerClient(registration);
        }
    }
}
