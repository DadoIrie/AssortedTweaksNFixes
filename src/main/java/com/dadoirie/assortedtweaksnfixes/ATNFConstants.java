package com.dadoirie.assortedtweaksnfixes;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ATNFConstants {
    public static final String MOD_ID = "assortedtweaksnfixes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation identifer(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    
    public static Logger getLogger(Class<?> cls) {
        return LoggerFactory.getLogger(MOD_ID + "/" + cls.getSimpleName());
    }
}