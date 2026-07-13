package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSoundInstance.class)
public interface AbstractSoundInstanceAccessor {

    @Accessor("x")
    void atnf$setX(double x);

    @Accessor("y")
    void atnf$setY(double y);

    @Accessor("z")
    void atnf$setZ(double z);
}