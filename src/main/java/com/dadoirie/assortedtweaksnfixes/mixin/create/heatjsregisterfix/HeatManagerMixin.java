package com.dadoirie.assortedtweaksnfixes.mixin.create.heatjsregisterfix;

import com.dadoirie.assortedtweaksnfixes.utils.rhino.RhinoEnumCacheRefresher;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.xiaohunao.create_heat_js.common.HeatData;
import com.xiaohunao.create_heat_js.common.HeatManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HeatManager.class, remap = false)
public abstract class HeatManagerMixin {

    @Inject(method = "registerHeatData", at = @At("TAIL"), remap = false)
    private void atf_refreshRhinoEnumCaches(HeatData heatData, CallbackInfo ci) {
        RhinoEnumCacheRefresher.refresh(HeatCondition.class, HeatCondition.values());
        RhinoEnumCacheRefresher.refresh(BlazeBurnerBlock.HeatLevel.class, BlazeBurnerBlock.HeatLevel.values());
    }
}