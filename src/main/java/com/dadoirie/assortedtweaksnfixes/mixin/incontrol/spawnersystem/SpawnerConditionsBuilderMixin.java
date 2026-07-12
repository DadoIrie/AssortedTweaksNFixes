package com.dadoirie.assortedtweaksnfixes.mixin.incontrol.spawnersystem;

import com.dadoirie.assortedtweaksnfixes.compat.incontrol.SpeciesClaimChunkData;
import mcjty.incontrol.spawner.SpawnerConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SpawnerConditions.Builder.class)
public abstract class SpawnerConditionsBuilderMixin implements SpeciesClaimChunkData.BuilderFlag {

    @Unique
    private boolean atnf$chunkReservation;

    @Override
    public void atnf$setChunkReservation(boolean value) {
        this.atnf$chunkReservation = value;
    }

    @Override
    public boolean atnf$getChunkReservation() {
        return this.atnf$chunkReservation;
    }
}