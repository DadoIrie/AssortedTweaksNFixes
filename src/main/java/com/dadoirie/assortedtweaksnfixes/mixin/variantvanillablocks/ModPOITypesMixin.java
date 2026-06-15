package com.dadoirie.assortedtweaksnfixes.mixin.variantvanillablocks;

import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.PoiTypesAccessor;
import com.dadoirie.assortedtweaksnfixes.mixin.neoforge.PoiStateSetAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.poi.PoiStateSet;
import net.xanthian.variantvanillablocks.utils.ModPOITypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(ModPOITypes.class)
public abstract class ModPOITypesMixin {

    /**
     * @author Dadoirie
     * @reason Fix POI extension errors when running Variant Vanilla Blocks (Fabric) via Sinytra Connector and Every Compat (aka WoodGood) on NeoForge.
     */
    @Overwrite
    public static void init() {
        assortedTweaksNFixes$patchPoi("_barrel", PoiTypes.FISHERMAN);
        assortedTweaksNFixes$patchPoi("_cartography_table", PoiTypes.CARTOGRAPHER);
        assortedTweaksNFixes$patchPoi("_fletching_table", PoiTypes.FLETCHER);
        assortedTweaksNFixes$patchPoi("_lectern", PoiTypes.LIBRARIAN);
        assortedTweaksNFixes$patchPoi("_beehive", PoiTypes.BEEHIVE);
        assortedTweaksNFixes$patchPoi("_grindstone", PoiTypes.WEAPONSMITH);
        assortedTweaksNFixes$patchPoi("_composter", PoiTypes.FARMER);
        assortedTweaksNFixes$patchPoi("_smithing_table", PoiTypes.TOOLSMITH);
        assortedTweaksNFixes$patchPoi("_smoker", PoiTypes.BUTCHER);
    }

    @Unique
    private static void assortedTweaksNFixes$patchPoi(String suffix, ResourceKey<PoiType> poiTypeKey) {
        Map<BlockState, Holder<PoiType>> poiStatesToType = PoiTypesAccessor.getBlockStateToPointOfInterestType();

        Holder<PoiType> holder = poiStatesToType.values().stream()
                .filter(h -> h.unwrapKey().map(k -> k.equals(poiTypeKey)).orElse(false))
                .findFirst()
                .orElseThrow();

        PoiType poiType = holder.value();
        Set<BlockState> addedStates = new HashSet<>();

        for (Block block : BuiltInRegistries.BLOCK) {
            ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(block);
            if (loc != null && loc.getNamespace().equals("variantvanillablocks") && loc.getPath().endsWith(suffix)) {
                for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                    poiStatesToType.putIfAbsent(state, holder);
                    addedStates.add(state);
                }
            }
        }

        if (poiType.matchingStates() instanceof PoiStateSet poiStateSet) {
            ((PoiStateSetAccessor) (Object) poiStateSet).invokeAddCustomStates(addedStates);
        }
    }
}