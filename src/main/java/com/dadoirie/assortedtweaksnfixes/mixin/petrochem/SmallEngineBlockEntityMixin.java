package com.dadoirie.assortedtweaksnfixes.mixin.petrochem;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import io.github.hadron13.petrochem.blocks.small_engine.EngineFuelRecipe;
import io.github.hadron13.petrochem.blocks.small_engine.SmallEngineBlockEntity;
import io.github.hadron13.petrochem.config.PetrochemConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(SmallEngineBlockEntity.class)
public abstract class SmallEngineBlockEntityMixin {

    @Shadow
    public EngineFuelRecipe currentFuel;

    @Shadow
    public ScrollValueBehaviour targetSpeed;

    @Shadow
    public float load;

    @Shadow
    public abstract float getGeneratedSpeed();

    @Inject(method = "getConsumption", at = @At("HEAD"), cancellable = true)
    private void atf_scaleConsumptionByRpm(CallbackInfoReturnable<Float> cir) {
        if (currentFuel == null) {
            cir.setReturnValue(0f);
            return;
        }
        float rpm = Mth.abs((float) targetSpeed.getValue());
        float rpmMultiplier = Math.max(1f, rpm / 3.9f);
        cir.setReturnValue(currentFuel.getConsumptionRate() * Math.max(load, 0.3f) * rpmMultiplier);
    }

    /**
     * @reason Removes Petrochem's flat SU override (configValue * 256 / RPM) and restores
     * vanilla Create's linear RPM scaling by returning the raw config value directly,
     * allowing Create's network to multiply by RPM internally.
     * @author AssortedTweaksNFixes
     */
    @Overwrite
    public float calculateAddedStressCapacity() {
        float speed = getGeneratedSpeed();
        if (speed == 0f) return 0f;
        return (float) Objects.requireNonNull(PetrochemConfig.server()
                        .kinetics
                        .stressValues
                        .getCapacity(((BlockEntity) (Object) this).getBlockState().getBlock()))
                        .getAsDouble();
    }
}