package com.dadoirie.assortedtweaksnfixes.mixin.petrochem.voltedelectrolyzer;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.compat.electroenergetics.ElectrolyzerElectricDevice;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import io.github.hadron13.petrochem.blocks.electrolyzer.ElectrolyzerBlockEntity;
import io.github.hadron13.petrochem.blocks.electrolyzer.ElectrolyzingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ElectrolyzerBlockEntity.class, remap = false)
public abstract class ElectrolyzerBlockEntityMixin extends MechanicalMixerBlockEntity implements ElectrolyzerElectricDevice.Machine {

    @Shadow
    public ScrollValueBehaviour speed;

    @Unique
    private double atnf$nodeVoltage;

    @Unique
    private double atnf$requiredVoltage;

    private ElectrolyzerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void atnf$setNodeVoltage(double voltage) {
        this.atnf$nodeVoltage = voltage;
    }

    @Inject(method = "registerCapabilities", at = @At("HEAD"), cancellable = true)
    private static void atf_removeFeCapability(RegisterCapabilitiesEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "addToGoggleTooltip", at = @At("HEAD"), cancellable = true)
    private void atf_removeGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void atf_electricTick(CallbackInfo ci) {
        ci.cancel();

        if (level != null && !level.isClientSide) {
            if (speed.value == 0)
                runningTicks++;

            atnf$requiredVoltage = currentRecipe instanceof ElectrolyzerElectricDevice.Electric electric
                    ? electric.atnf$voltage()
                    : 0.0;

            if (!isRunning())
                currentRecipe = null;

            sendData();
        }

        boolean undervolted = atnf$requiredVoltage > 0
                && atnf$nodeVoltage < ElectrolyzerElectricDevice.minVoltage(atnf$requiredVoltage);

        if (undervolted) {
            int savedRunningTicks = runningTicks;
            super.tick();
            if (running)
                runningTicks = savedRunningTicks;
        } else {
            super.tick();
        }
    }

    @Inject(method = "getSpeed", at = @At("HEAD"), cancellable = true)
    private void atf_electricSpeed(CallbackInfoReturnable<Float> cir) {
        if (speed == null || atnf$nodeVoltage < ElectrolyzerElectricDevice.ENERGIZED_MIN_VOLTAGE) {
            cir.setReturnValue(0f);
            return;
        }
        if (atnf$requiredVoltage > 0
                && atnf$nodeVoltage < atnf$requiredVoltage * ElectrolyzerElectricDevice.RUN_MIN_FACTOR) {
            cir.setReturnValue(0f);
            return;
        }
        cir.setReturnValue(128f * (speed.value / 100f));
    }

    @Redirect(method = "applyBasinRecipe",
            at = @At(value = "INVOKE",
                    target = "Lio/github/hadron13/petrochem/blocks/electrolyzer/ElectrolyzingRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"))
    private boolean atf_wasteOnOvervolt(BasinBlockEntity basin, Recipe<?> recipe) {
        if (recipe instanceof ElectrolyzingRecipe electrolyzing
                && recipe instanceof ElectrolyzerElectricDevice.Electric electric
                && atnf$nodeVoltage > ElectrolyzerElectricDevice.maxVoltage(electric.atnf$voltage())) {
            boolean consumed = ElectrolyzingRecipe.apply(basin, atnf$buildWasteRecipe(electrolyzing));
            if (consumed)
                atnf$showWasteEffect(basin);
            return consumed;
        }
        return ElectrolyzingRecipe.apply(basin, recipe);
    }

    @Unique
    private void atnf$showWasteEffect(BasinBlockEntity basin) {
        BlockPos basinPos = basin.getBlockPos();
        if (level == null || !level.isLoaded(basinPos))
            return;
        Vec3 pos = basinPos.getCenter();
        ((ServerLevel) level).sendParticles(ParticleTypes.LARGE_SMOKE,
                pos.x, pos.y, pos.z, 8, 0.5, 0.25, 0.5, 0.0);
        level.playSound(null, basinPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6f, 1.0f);
    }

    @Unique
    private ElectrolyzingRecipe atnf$buildWasteRecipe(ElectrolyzingRecipe source) {
        ElectrolyzingRecipe.Builder<ElectrolyzingRecipe> builder = new ElectrolyzingRecipe.Builder<>(
                ElectrolyzingRecipe::new,
                ATNFConstants.identifer("electrolyzer_waste"));

        for (SizedFluidIngredient fluidIngredient : source.getFluidIngredients())
            builder.require(fluidIngredient);
        for (Ingredient ingredient : source.getIngredients())
            builder.require(ingredient);

        return builder.build();
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void atf_writeVoltage(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        compound.putDouble("atnf_voltage", atnf$nodeVoltage);
        compound.putDouble("atnf_required_voltage", atnf$requiredVoltage);
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void atf_readVoltage(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        atnf$nodeVoltage = compound.getDouble("atnf_voltage");
        atnf$requiredVoltage = compound.getDouble("atnf_required_voltage");
    }
}