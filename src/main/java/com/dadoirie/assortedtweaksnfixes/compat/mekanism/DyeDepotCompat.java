package com.dadoirie.assortedtweaksnfixes.compat.mekanism;

import com.ninni.dye_depot.registry.DDDyes;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DyeDepotCompat {

    public static final DeferredRegister<Chemical> PIGMENTS =
            DeferredRegister.create(MekanismAPI.CHEMICAL_REGISTRY_NAME, "assortedtweaksnfixes");

    public static void register(IEventBus modBus) {
        for (DDDyes dye : DDDyes.values()) {
            String name = "dd_" + dye.getSerializedName();
            PIGMENTS.register(name, () -> new Chemical(ChemicalBuilder.pigment().tint(dye.getColor())));
        }
        PIGMENTS.register(modBus);
    }
}