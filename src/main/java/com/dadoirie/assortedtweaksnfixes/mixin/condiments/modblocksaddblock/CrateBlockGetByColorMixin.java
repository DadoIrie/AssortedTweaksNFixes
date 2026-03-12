package com.dadoirie.assortedtweaksnfixes.mixin.condiments.modblocksaddblock;

import com.dadoirie.assortedtweaksnfixes.compat.condiments.DyeDepotCrateRegistry;
import dev.chililisoup.condiments.block.CrateBlock;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(CrateBlock.class)
public class CrateBlockGetByColorMixin {

    @Inject(method = "getBlockByColor", at = @At("RETURN"), cancellable = true)
    private static void assortedTweaks$addDyeDepotCrates(DyeColor color, CallbackInfoReturnable<Block> cir) {
        if (color == null) return;

        Supplier<Block> supplier = DyeDepotCrateRegistry.DYE_CRATES.get(color);
        if (supplier != null) {
            cir.setReturnValue(supplier.get());
        }
    }
}