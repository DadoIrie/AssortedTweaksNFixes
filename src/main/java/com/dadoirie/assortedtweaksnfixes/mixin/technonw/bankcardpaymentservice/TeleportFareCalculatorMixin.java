package com.dadoirie.assortedtweaksnfixes.mixin.technonw.bankcardpaymentservice;

import com.technophobia.technonw.payment.TeleportFareCalculator;
import net.blay09.mods.waystones.api.Waystone;
import net.blay09.mods.waystones.api.WaystoneTeleportContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TeleportFareCalculator.class)
public class TeleportFareCalculatorMixin {

    @Inject(method = "calculateCost", at = @At("HEAD"), cancellable = true)
    private void technonw$tagCurrentWaystone(WaystoneTeleportContext context, CallbackInfoReturnable<Integer> cir) {
        if (context.getFromWaystone().isPresent() && context.getTargetWaystone() != null) {
            Waystone from = context.getFromWaystone().get();
            Waystone to = context.getTargetWaystone();
            if (from.getDimension() == to.getDimension() && from.getPos().equals(to.getPos())) {
                cir.setReturnValue(-1);
            }
        }
    }
}