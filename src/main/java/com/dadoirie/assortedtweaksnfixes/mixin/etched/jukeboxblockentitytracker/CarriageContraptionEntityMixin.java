package com.dadoirie.assortedtweaksnfixes.mixin.etched.jukeboxblockentitytracker;

import com.dadoirie.assortedtweaksnfixes.compat.etched.ContraptionPositionLogger;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarriageContraptionEntity.class)
public abstract class CarriageContraptionEntityMixin {

    @Inject(method = "setCarriage", at = @At("TAIL"))
    private void atf_registerTrainCarriage(Carriage carriage, CallbackInfo ci) {
        ContraptionPositionLogger.registerTrain(
                (CarriageContraptionEntity) (Object) this,
                carriage
        );
    }
}
