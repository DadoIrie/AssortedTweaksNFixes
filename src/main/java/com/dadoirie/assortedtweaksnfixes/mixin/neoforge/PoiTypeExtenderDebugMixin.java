package com.dadoirie.assortedtweaksnfixes.mixin.neoforge;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixes;
import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.poi.PoiTypeExtender;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(PoiTypeExtender.class)
public class PoiTypeExtenderDebugMixin {

    @Unique
    private static final Logger assortedTweaksNFixes$LOGGER = AssortedTweaksNFixesConstants.getLogger(AssortedTweaksNFixes.class);

    @Unique
    private static final Set<ResourceKey<PoiType>> assortedTweaksNFixes$modifiedTypes = new HashSet<>();

    @Inject(method = "register", at = @At("HEAD"))
    private static void onRegister(ResourceKey<PoiType> typeKey, Set<BlockState> states, CallbackInfo ci) {
        assortedTweaksNFixes$modifiedTypes.add(typeKey);
    }

    @Inject(method = "extendPoiTypes", at = @At("RETURN"))
    private static void onExtendPoiTypesReturn(CallbackInfo ci) {
        for (ResourceKey<PoiType> typeKey : assortedTweaksNFixes$modifiedTypes) {
            PoiType poiType = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(typeKey);
            Set<String> blocks = poiType.matchingStates().stream()
                    .map(state -> BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString())
                    .collect(Collectors.toSet());

            assortedTweaksNFixes$LOGGER.info("PoiTypeExtender final call for {}\n[BLOCKS]: {}",
                    typeKey.location(),
                    blocks);
        }
        assortedTweaksNFixes$modifiedTypes.clear();
    }
}