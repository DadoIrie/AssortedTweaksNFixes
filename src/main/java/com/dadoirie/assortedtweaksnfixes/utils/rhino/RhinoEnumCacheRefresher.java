package com.dadoirie.assortedtweaksnfixes.utils.rhino;

import com.dadoirie.assortedtweaksnfixes.mixin.create.heatjsregisterfix.EnumTypeInfoAccessor;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.List;

public final class RhinoEnumCacheRefresher {

    private RhinoEnumCacheRefresher() {}

    public static void refresh(Class<?> enumClass, Object[] liveValues) {
        EnumTypeInfoAccessor accessor = (EnumTypeInfoAccessor) TypeInfo.of(enumClass);
        accessor.atnf$setConstants(List.of(liveValues));
        accessor.atnf$setConstantMap(null);
    }
}