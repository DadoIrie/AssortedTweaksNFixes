package com.dadoirie.assortedtweaksnfixes.mixin.petrochem.voltedelectrolyzer;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Supplier;

@Mixin(ProcessingRecipeParams.class)
public interface ProcessingRecipeParamsInvoker {

    @Invoker(value = "codec", remap = false)
    static <P extends ProcessingRecipeParams> MapCodec<P> atnf$baseCodec(Supplier<P> factory) {
        throw new AssertionError();
    }
}
