package com.dadoirie.assortedtweaksnfixes.mixin.create.heatjsregisterfix;

import dev.latvian.mods.rhino.type.EnumTypeInfo;
import java.util.List;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EnumTypeInfo.class, remap = false)
public interface EnumTypeInfoAccessor {

    @Accessor(value = "constants", remap = false)
    void atnf$setConstants(List<Object> constants);

    @Accessor(value = "constantMap", remap = false)
    void atnf$setConstantMap(Map<String, Object> constantMap);
}