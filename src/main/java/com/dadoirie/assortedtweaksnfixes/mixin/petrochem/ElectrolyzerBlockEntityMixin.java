package com.dadoirie.assortedtweaksnfixes.mixin.petrochem;

import io.github.hadron13.petrochem.blocks.electrolyzer.ElectrolyzerBlockEntity;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Removes the electrolyzer's FE input capability on all sides. The machine
 * is powered exclusively through CEE wire terminals instead.
 * <p>
 * This mixin is gated (mixin_requirements.json) on both petrochem and
 * electroenergetics being present, so the cancel is unconditional here.
 */
@Mixin(ElectrolyzerBlockEntity.class)
public abstract class ElectrolyzerBlockEntityMixin {

    @Inject(method = "registerCapabilities", at = @At("HEAD"), cancellable = true)
    private static void atf_removeFeCapability(RegisterCapabilitiesEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "addToGoggleTooltip", at = @At("HEAD"), cancellable = true)
    private void atf_removeGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}