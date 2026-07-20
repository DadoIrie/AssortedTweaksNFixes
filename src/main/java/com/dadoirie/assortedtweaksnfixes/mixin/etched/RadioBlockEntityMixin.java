package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// clearContent() nulls url unconditionally before drops are ever computed; the client still needs it
// nulled (its own playRadio(null, ...) call right below is what stops the sound), but the server has
// no such call to preserve - only the field write is skipped there, so the value survives for the drop
@Mixin(RadioBlockEntity.class)
public abstract class RadioBlockEntityMixin {

    @Shadow
    private String url;

    @Redirect(
            method = "clearContent",
            at = @At(
                    value = "FIELD",
                    target = "Lgg/moonflower/etched/common/blockentity/RadioBlockEntity;url:Ljava/lang/String;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void atnf$keepUrlServerSide(RadioBlockEntity instance, String value) {
        if (instance.getLevel() == null || instance.getLevel().isClientSide()) {
            this.url = value;
        }
    }
}
