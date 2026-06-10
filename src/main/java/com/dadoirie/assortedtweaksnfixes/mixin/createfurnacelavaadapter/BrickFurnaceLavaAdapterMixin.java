package com.dadoirie.assortedtweaksnfixes.mixin.createfurnacelavaadapter;

import com.dadoirie.assortedtweaksnfixes.mixin.createfurnacelavaadapter.brickfurnacelavaadapter.AbstractBrickFurnaceBlockEntityAccessor;
import com.dadoirie.assortedtweaksnfixes.mixin.createfurnacelavaadapter.brickfurnacelavaadapter.AbstractFurnaceBlockEntityAccessor;
import net.mcreator.createfurnacelavaadapter.procedures.FurnaceLavaAdapterOnTickUpdateProcedure;
import de.cech12.brickfurnace.blockentity.AbstractBrickFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FurnaceLavaAdapterOnTickUpdateProcedure.class, remap = false)
public class BrickFurnaceLavaAdapterMixin {

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void onExecute(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        BlockPos adapterPos = BlockPos.containing(x, y, z);
        BlockState adapterState = world.getBlockState(adapterPos);

        var property = adapterState.getBlock().getStateDefinition().getProperty("facing");
        if (property == null || !(adapterState.getValue(property) instanceof Direction dir)) {
            return;
        }

        BlockPos targetPos = adapterPos.relative(dir);
        BlockEntity be = world.getBlockEntity(targetPos);

        if (!(be instanceof AbstractBrickFurnaceBlockEntity brickFurnace) || !(world instanceof Level level) || !(world instanceof ILevelExtension ext)) {
            return;
        }

        IFluidHandler fluidHandler = ext.getCapability(Capabilities.FluidHandler.BLOCK, adapterPos, null);
        if (fluidHandler == null || fluidHandler.getFluidInTank(0).getAmount() < 1000) {
            ci.cancel();
            return;
        }

        ci.cancel();

        IItemHandler itemHandler = ext.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, null);
        if (itemHandler == null) {
            return;
        }

        ItemStack input = itemHandler.getStackInSlot(0);
        ItemStack fuel  = itemHandler.getStackInSlot(1);
        ItemStack output = itemHandler.getStackInSlot(2);

        BlockState targetState = world.getBlockState(targetPos);
        boolean isLit = targetState.hasProperty(AbstractFurnaceBlock.LIT) && targetState.getValue(AbstractFurnaceBlock.LIT);

        if (fuel.isEmpty() && !input.isEmpty() && !isLit) {
            RecipeHolder<?> activeRecipe = brickFurnace.getRecipe();
            if (activeRecipe == null) {
                return;
            }

            if (!((AbstractBrickFurnaceBlockEntityAccessor) brickFurnace).invokeCanBurn(activeRecipe)) {
                return;
            }

            ItemStack recipeOutput = activeRecipe.value().getResultItem(level.registryAccess());
            if (!output.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(output, recipeOutput) || (output.getCount() + recipeOutput.getCount() > output.getMaxStackSize())) {
                    return;
                }
            }

            ItemStack mockLavaBucket = new ItemStack(Items.LAVA_BUCKET);
            int dynamicBurnTime = ((AbstractFurnaceBlockEntityAccessor) brickFurnace).invokeGetBurnDuration(mockLavaBucket);

            if (dynamicBurnTime <= 0) {
                return;
            }

            ContainerData data = brickFurnace.getContainerData();
            data.set(AbstractBrickFurnaceBlockEntity.BURN_TIME, dynamicBurnTime);

            world.setBlock(targetPos, targetState.setValue(AbstractFurnaceBlock.LIT, true), 3);
            fluidHandler.drain(1000, FluidAction.EXECUTE);
            brickFurnace.setChanged();
        }
    }
}