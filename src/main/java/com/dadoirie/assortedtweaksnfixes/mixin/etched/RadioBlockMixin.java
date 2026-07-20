package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import gg.moonflower.etched.common.block.RadioBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RadioBlock.class)
public abstract class RadioBlockMixin {

    @Redirect(
            method = "onRemove",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;levelEvent(ILnet/minecraft/core/BlockPos;I)V"
            )
    )
    private void atnf$keepSoundWhileMoved(Level level, int type, BlockPos pos, int data,
                                          BlockState state, Level unusedLevel, BlockPos unusedPos,
                                          BlockState newState, boolean moving) {
        if (!moving) {
            level.levelEvent(type, pos, data);
        }
    }
}