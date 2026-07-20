package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gg.moonflower.etched.api.sound.AbstractOnlineSoundInstance;
import net.minecraft.Util;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Mixin(AbstractOnlineSoundInstance.class)
public abstract class AbstractOnlineSoundInstanceMixin {

    @WrapMethod(method = "getStream(Lnet/minecraft/client/sounds/SoundBufferLibrary;Lnet/minecraft/client/resources/sounds/Sound;Z)Ljava/util/concurrent/CompletableFuture;")
    private CompletableFuture<AudioStream> atnf$resolveOffRenderThread(SoundBufferLibrary loader, Sound sound, boolean repeatInstantly,
                                                                       Operation<CompletableFuture<AudioStream>> original) {
        return CompletableFuture.supplyAsync(() -> original.call(loader, sound, repeatInstantly), Util.nonCriticalIoPool())
                .thenCompose(Function.identity());
    }
}
