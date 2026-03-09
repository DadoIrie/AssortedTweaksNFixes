package com.dadoirie.assortedtweaksnfixes.compat.mekanism;

import com.ninni.dye_depot.registry.DDDyes;
import mekanism.common.registries.MekanismChemicals;

public class DyeDepotCompat {

    public static void populatePigments() {
        for (DDDyes dye : DDDyes.values()) {
            String name = "dd_" + dye.getSerializedName();

            MekanismChemicals.CHEMICALS.registerPigment(name, dye.getColor());
        }
    }
}