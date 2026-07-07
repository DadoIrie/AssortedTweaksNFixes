package com.dadoirie.assortedtweaksnfixes.mixin.prettybeaches;

import net.blay09.mods.prettybeaches.FloodingManager;
import net.blay09.mods.prettybeaches.config.PrettyBeachesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FloodingManager.class)
public class FloodingManagerMixin {

    @Inject(
            method = "populateWater",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void assortedtweaks$protectLandedSolidBlocks(
            Level level,
            BlockPos pos,
            int depth,
            CallbackInfo ci
    ) {
        BlockState currentState = level.getBlockState(pos);

        if (!currentState.isAir() && currentState.getFluidState().isEmpty()) {
            if (PrettyBeachesConfig.isBlockAffected(currentState.getBlock())) {
                ci.cancel();
            }
        }
    }
}