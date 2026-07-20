package com.dadoirie.assortedtweaksnfixes.mixin.cablefacades.coveredvisualizer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.portingdeadmods.cable_facades.CFConfig;
import com.portingdeadmods.cable_facades.utils.FacadeUtils;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SafeBlockEntityRenderer.class)
public abstract class CoveredSafeRendererMixin<T extends BlockEntity> {

    // Fires only on the /flywheel backend off path (or if Flywheel fails to init).
    // CF already draws the facade via its own AddSectionGeometry pipeline, so we
    // suppress the animated BE render rather than re-render anything.
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void atnf$skipWhenFacaded(
            T be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource,
            int light, int overlay, CallbackInfo ci) {

        Level level = be.getLevel();
        if (level == null) {
            return;
        }
        BlockPos pos = be.getBlockPos();
        if (FacadeUtils.hasFacade(level, pos)
                && CFConfig.shouldHideWhenFacaded(be.getBlockState().getBlock())) {
            ci.cancel();
        }
    }
}