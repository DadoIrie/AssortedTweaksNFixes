package com.dadoirie.assortedtweaksnfixes.mixin.condiments;

import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Supplier;

@Mixin(ModBlocks.class)
public interface ModBlocksAddBlockMixin {
    @Invoker("addBlock")
    static Supplier<Block> invokeAddBlock(ModBlocks.Params params) {
        throw new AssertionError();
    }
}