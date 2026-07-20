package com.dadoirie.assortedtweaksnfixes.mixin.incontrol.spawnersystem;

import com.dadoirie.assortedtweaksnfixes.compat.incontrol.SpeciesClaimChunkData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mcjty.incontrol.setup.Config;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Config.class)
public abstract class ConfigMixin {

    @WrapOperation(
            method = "register",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/ModConfigSpec$Builder;pop()Lnet/neoforged/neoforge/common/ModConfigSpec$Builder;"
            )
    )
    private static ModConfigSpec.Builder atnf$appendReservationSettings(ModConfigSpec.Builder builder, Operation<ModConfigSpec.Builder> original) {
        SpeciesClaimChunkData.CHUNK_RESERVATION_VERIFY_TICKS = builder
                .comment("How many ticks a chunk species reservation stays trusted before the claiming species is re-verified on contested spawn attempts. Use 0 to re-verify on every contested attempt.")
                .defineInRange("chunkReservationVerifyTicks", 600, 0, 72000);
        return original.call(builder);
    }
}