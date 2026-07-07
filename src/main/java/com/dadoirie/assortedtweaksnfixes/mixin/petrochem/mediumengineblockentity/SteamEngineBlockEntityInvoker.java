package com.dadoirie.assortedtweaksnfixes.mixin.petrochem.mediumengineblockentity;

import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.SteamEngineBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SteamEngineBlockEntity.class)
public interface SteamEngineBlockEntityInvoker {

    @Invoker("getShaft")
    PoweredShaftBlockEntity invokeGetShaft();
}