package com.dadoirie.assortedtweaksnfixes.mixin.neoforge;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.poi.PoiStateSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(PoiStateSet.class)
public interface PoiStateSetAccessor {

    @Invoker("addCustomStates")
    void invokeAddCustomStates(Set<BlockState> states);
}