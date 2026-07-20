package com.dadoirie.assortedtweaksnfixes.mixin.incontrol.spawnersystem;

import com.dadoirie.assortedtweaksnfixes.compat.incontrol.SpeciesClaimChunkData;
import com.google.gson.JsonObject;
import mcjty.incontrol.spawner.SpawnerConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerConditions.class)
public abstract class SpawnerConditionsMixin implements SpeciesClaimChunkData.ConditionsFlag {

    @Unique
    private boolean atnf$chunkReservation;

    @Inject(method = "parse", at = @At("HEAD"))
    private static void atnf$extractChunkReservation(JsonObject object, SpawnerConditions.Builder builder, CallbackInfo callback) {
        if (object.has("chunkreservation")) {
            boolean value = object.getAsJsonPrimitive("chunkreservation").getAsBoolean();
            object.remove("chunkreservation");
            ((SpeciesClaimChunkData.BuilderFlag) builder).atnf$setChunkReservation(value);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void atnf$copyChunkReservation(SpawnerConditions.Builder builder, CallbackInfo callback) {
        this.atnf$chunkReservation = ((SpeciesClaimChunkData.BuilderFlag) builder).atnf$getChunkReservation();
    }

    @Override
    public boolean atnf$chunkReservation() {
        return this.atnf$chunkReservation;
    }
}