package com.dadoirie.assortedtweaksnfixes.mixin.minecraft;

import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(BlockItem.class)
public abstract class ReversePlacementMixin {
    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void atnf_reversePlacementOnVerticalFace(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state == null) {
            return;
        }
        if (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown()) {
            return;
        }
        if (context.getClickedFace().getAxis() != Direction.Axis.Y) {
            return;
        }
        if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction current = state.getValue(BlockStateProperties.FACING);
            cir.setReturnValue(state.setValue(BlockStateProperties.FACING, current.getOpposite()));
        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction current = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            cir.setReturnValue(state.setValue(BlockStateProperties.HORIZONTAL_FACING, current.getOpposite()));
        }
    }
}