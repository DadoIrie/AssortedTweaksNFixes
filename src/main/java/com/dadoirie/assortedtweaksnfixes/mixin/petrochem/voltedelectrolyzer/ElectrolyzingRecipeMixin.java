package com.dadoirie.assortedtweaksnfixes.mixin.petrochem.voltedelectrolyzer;

import com.dadoirie.assortedtweaksnfixes.compat.electroenergetics.ElectrolyzerElectricDevice;

import io.github.hadron13.petrochem.blocks.electrolyzer.ElectrolyzingRecipe;
import io.github.hadron13.petrochem.blocks.electrolyzer.EnergyRecipeParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ElectrolyzingRecipe.class, remap = false)
public abstract class ElectrolyzingRecipeMixin implements ElectrolyzerElectricDevice.Electric {

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

    @Inject(method = "<init>", at = @At("TAIL"))
    private void atf_copyElectric(EnergyRecipeParams params, CallbackInfo ci) {
        atnf$setElectric(((ElectrolyzerElectricDevice.Electric) params).atnf$kilowatts(),
                ((ElectrolyzerElectricDevice.Electric) params).atnf$voltage());
    }
}