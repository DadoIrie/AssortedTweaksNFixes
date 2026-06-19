package com.dadoirie.assortedtweaksnfixes.mixin.minecraft;

import com.dadoirie.assortedtweaksnfixes.client.KeybindHandler;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class GameOptionsMixin {

    @Inject(method = "save", at = @At("TAIL"))
    private void onSave(CallbackInfo ci) {
        KeybindHandler.getInstance().syncKeybinds();
    }
}