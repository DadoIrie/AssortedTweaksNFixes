package com.dadoirie.assortedtweaksnfixes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssortedTweaksNFixesConstants {
    public static final String MOD_ID = "assortedtweaksnfixes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static Logger getLogger(Class<?> cls) {
        return LoggerFactory.getLogger(MOD_ID + "/" + cls.getSimpleName());
    }
}