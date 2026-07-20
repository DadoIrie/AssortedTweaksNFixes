package com.dadoirie.assortedtweaksnfixes.mixin.create;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HeatCondition.class, remap = false)
public abstract class HeatJsRegisterFixMixin {

    @Shadow
    @Final
    @Mutable
    public static StreamCodec<ByteBuf, HeatCondition> STREAM_CODEC;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void atnf_replaceStreamCodec(CallbackInfo ci) {
        STREAM_CODEC = StreamCodec.of(
                (buffer, value) -> VarInt.write(buffer, value.ordinal()),
                buffer -> HeatCondition.values()[VarInt.read(buffer)]
        );
    }
}