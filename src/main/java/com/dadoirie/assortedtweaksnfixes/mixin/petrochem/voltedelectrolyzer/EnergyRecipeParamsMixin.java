package com.dadoirie.assortedtweaksnfixes.mixin.petrochem.voltedelectrolyzer;

import com.dadoirie.assortedtweaksnfixes.compat.electroenergetics.ElectrolyzerElectricDevice;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.hadron13.petrochem.blocks.electrolyzer.EnergyRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = EnergyRecipeParams.class, remap = false)
public abstract class EnergyRecipeParamsMixin implements ElectrolyzerElectricDevice.Electric {

    @Unique
    private double atnf$kilowatts;
    @Unique
    private double atnf$voltage;

    @Override
    public double atnf$kilowatts() {
        return atnf$kilowatts;
    }

    @Override
    public double atnf$voltage() {
        return atnf$voltage;
    }

    @Override
    public void atnf$setElectric(double kilowatts, double voltage) {
        this.atnf$kilowatts = kilowatts;
        this.atnf$voltage = voltage;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void atnf$swapCodec(CallbackInfo ci) {
        EnergyRecipeParams.CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ProcessingRecipeParamsInvoker.atnf$baseCodec(EnergyRecipeParams::new).forGetter(Function.identity()),
                Codec.DOUBLE.fieldOf("kilowatts").forGetter(p -> ((ElectrolyzerElectricDevice.Electric) p).atnf$kilowatts()),
                Codec.DOUBLE.fieldOf("voltage").forGetter(p -> ((ElectrolyzerElectricDevice.Electric) p).atnf$voltage())
        ).apply(instance, (params, kilowatts, voltage) -> {
            ((ElectrolyzerElectricDevice.Electric) params).atnf$setElectric(kilowatts, voltage);
            return params;
        }));
    }

    @Inject(method = "encode", at = @At("TAIL"))
    private void atnf$encodeElectric(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        ByteBufCodecs.DOUBLE.encode(buffer, atnf$kilowatts);
        ByteBufCodecs.DOUBLE.encode(buffer, atnf$voltage);
    }

    @Inject(method = "decode", at = @At("TAIL"))
    private void atnf$decodeElectric(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        atnf$kilowatts = ByteBufCodecs.DOUBLE.decode(buffer);
        atnf$voltage = ByteBufCodecs.DOUBLE.decode(buffer);
    }
}