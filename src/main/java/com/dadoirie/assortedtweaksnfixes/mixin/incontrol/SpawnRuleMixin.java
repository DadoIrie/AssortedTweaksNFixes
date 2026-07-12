package com.dadoirie.assortedtweaksnfixes.mixin.incontrol;

import com.dadoirie.assortedtweaksnfixes.compat.incontrol.InControlRuleKeys;
import mcjty.incontrol.rules.SpawnRule;
import mcjty.incontrol.tools.typed.Attribute;
import mcjty.incontrol.tools.typed.GenericAttributeMapFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnRule.class)
public abstract class SpawnRuleMixin {

    @Shadow
    @Final
    private static GenericAttributeMapFactory FACTORY;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void atf_registerInhabitedThresholdKey(CallbackInfo callback) {
        FACTORY.attribute(Attribute.create(InControlRuleKeys.INHABITED_THRESHOLD));
    }
}