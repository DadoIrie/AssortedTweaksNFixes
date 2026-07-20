package com.dadoirie.assortedtweaksnfixes.mixin.petrochem;

import com.dadoirie.assortedtweaksnfixes.mixin.petrochem.mediumengineblockentity.SteamEngineBlockEntityInvoker;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import io.github.hadron13.petrochem.blocks.medium_engine.MediumEngineBlock;
import io.github.hadron13.petrochem.blocks.medium_engine.MediumEngineBlockEntity;
import io.github.hadron13.petrochem.blocks.small_engine.EngineFuelRecipe;
import io.github.hadron13.petrochem.register.PetrochemBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MediumEngineBlockEntity.class)
public abstract class MediumEngineBlockEntityMixin {

    @Shadow
    public EngineFuelRecipe currentFuel;

    @Shadow
    public ScrollValueBehaviour targetSpeed;

    @Shadow
    public float load;

    @Inject(method = "getConsumption", at = @At("HEAD"), cancellable = true)
    private void atnf$scaleConsumptionByRpm(CallbackInfoReturnable<Float> cir) {
        if (currentFuel == null) {
            cir.setReturnValue(0f);
            return;
        }
        float rpm = Mth.abs((float) targetSpeed.getValue());
        float rpmMultiplier = Math.max(1f, rpm / 6.5f);
        cir.setReturnValue(currentFuel.getConsumptionRate() * Math.max(load, 0.5f) * rpmMultiplier);
    }

    /**
     * @reason Removes efficiency / targetSpeed division which flattened SU output regardless of RPM.
     * Passes efficiency = 1.0 directly to shaft.update() allowing PoweredShaftBlockEntity
     * to scale SU linearly with RPM via its own capacity system.
     * @author AssortedTweaksNFixes
     */
    @Overwrite
    public void updateRotation() {
        MediumEngineBlockEntity self = (MediumEngineBlockEntity) (Object) this;
        PoweredShaftBlockEntity shaft = ((SteamEngineBlockEntityInvoker) this).invokeGetShaft();
        if (shaft != null) {
            if (this.currentFuel == null) {
                if (!shaft.getBlockPos().subtract(self.getBlockPos()).equals(shaft.enginePos)) {
                    return;
                }
                if (shaft.engineEfficiency == 0.0F) {
                    return;
                }
                shaft.update(self.getBlockPos(), 0, 0.0F);
                return;
            }
            Direction facing = MediumEngineBlock.getFacing(self.getBlockState());
            BlockState shaftState = shaft.getBlockState();
            Direction.Axis targetAxis = Axis.X;
            Block var6 = shaftState.getBlock();
            if (var6 instanceof IRotate ir) {
                targetAxis = ir.getRotationAxis(shaftState);
            }
            boolean verticalTarget = targetAxis == Axis.Y;
            BlockState blockState = self.getBlockState();
            if (PetrochemBlocks.MEDIUM_ENGINE.has(blockState)) {
                if (facing.getAxis() == Axis.Y) {
                    facing = blockState.getValue(MediumEngineBlock.FACING);
                }
                float efficiency = this.currentFuel != null ? 1.0F : 0.0F;
                int rotationSpeed = efficiency == 0.0F ? 1 : (verticalTarget ? 1 : (int) GeneratingKineticBlockEntity.convertToDirection(1.0F, facing));
                if (targetAxis == Axis.Z) {
                    rotationSpeed *= -1;
                }
                float shaftSpeed = shaft.getTheoreticalSpeed();
                if (shaft.hasSource() && shaftSpeed != 0.0F && rotationSpeed != 0 && shaftSpeed > 0.0F != rotationSpeed > 0) {
                    rotationSpeed *= -1;
                }
                shaft.update(self.getBlockPos(), rotationSpeed * this.targetSpeed.getValue(), efficiency);
            }
        }
    }
}