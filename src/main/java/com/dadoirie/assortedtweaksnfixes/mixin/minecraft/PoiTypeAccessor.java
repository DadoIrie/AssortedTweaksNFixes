package com.dadoirie.assortedtweaksnfixes.mixin.minecraft;

import java.util.Set;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PoiType.class})
public interface PoiTypeAccessor {
    @Accessor("matchingStates")
    Set<BlockState> getMatchingStates();

    @Mutable
    @Accessor("matchingStates")
    void setMatchingStates(Set<BlockState> var1);
}
