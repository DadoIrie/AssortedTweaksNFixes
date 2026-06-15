package com.dadoirie.assortedtweaksnfixes.mixin.brickfurnace;

import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.PoiTypesAccessor;
import com.dadoirie.assortedtweaksnfixes.mixin.neoforge.PoiStateSetAccessor;
import de.cech12.brickfurnace.CommonLoader;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.poi.PoiStateSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Mixin(CommonLoader.class)
public abstract class CommonLoaderMixin {

    /**
     * @author Dadoirie
     * @reason fixing PoiTypeExtender errors which rise when using fabric mods with connector on neoforge
     */
    @Overwrite
    private static void replacePoiStates(
            Function<ResourceKey<PoiType>, PoiType> poiTypeGetter,
            Function<ResourceKey<PoiType>, Holder<PoiType>> poiTypeHolderGetter,
            ResourceKey<PoiType> poiTypeKey,
            Block addBlock) {

        if (addBlock == null) return;

        Set<BlockState> addedStates = new HashSet<>(addBlock.getStateDefinition().getPossibleStates());

        Map<BlockState, Holder<PoiType>> poiStatesToType = PoiTypesAccessor.getBlockStateToPointOfInterestType();
        for (BlockState state : addedStates) {
            poiStatesToType.putIfAbsent(state, poiTypeHolderGetter.apply(poiTypeKey));
        }

        PoiType poiType = poiTypeGetter.apply(poiTypeKey);
        if (poiType.matchingStates() instanceof PoiStateSet poiStateSet) {
            ((PoiStateSetAccessor) (Object) poiStateSet).invokeAddCustomStates(addedStates);
        }
    }
}