package com.dadoirie.assortedtweaksnfixes.mixin.brickfurnace;

import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.AbstractFurnaceBlockEntityAccessor;
import de.cech12.brickfurnace.blockentity.AbstractBrickFurnaceBlockEntity;
import com.dadoirie.assortedtweaksnfixes.mixin.brickfurnace.abstractbrickfurnaceblockentity.AbstractBrickFurnaceBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractBrickFurnaceBlockEntity.class, remap = false)
public class AbstractBrickFurnaceBlockEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private static void assortedtweaks_brickFluidFuel(
            Level level, BlockPos pos, BlockState state,
            AbstractBrickFurnaceBlockEntity entity, CallbackInfo ci) {

        ItemStack fuel = entity.getItem(1);
        ItemStack input = entity.getItem(0);
        ItemStack output = entity.getItem(2);

        if (!fuel.isEmpty() || input.isEmpty() || entity.litTime > 0) return;

        RecipeHolder<? extends AbstractCookingRecipe> activeRecipe = entity.getRecipe();
        if (activeRecipe == null) return;

        if (!AbstractFurnaceBlockEntityAccessor.invokeCanBurn(level.registryAccess(), activeRecipe, entity.items, entity.getMaxStackSize(), entity)) return;

        ItemStack recipeOutput = activeRecipe.value().getResultItem(level.registryAccess());
        if (!output.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(output, recipeOutput) || (output.getCount() + recipeOutput.getCount() > output.getMaxStackSize())) {
                return;
            }
        }

        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, state, entity, null);
        if (handler == null) return;

        int available = handler.getFluidInTank(0).getAmount();

        // double burnFactor = Services.CONFIG.getBurnTimeFactor();
        int totalBurnTime = (int) (((AbstractFurnaceBlockEntityAccessor) entity).invokeGetBurnDuration(new ItemStack(Items.LAVA_BUCKET)));
        if (totalBurnTime <= 0) return;

        int cookTimeTicks = ((AbstractBrickFurnaceBlockEntityAccessor) entity).invokeGetTotalCookTime(activeRecipe);
        int itemCount = Math.min(input.getCount(), 64);

        int totalTicksNeeded = itemCount * cookTimeTicks;
        int mbNeeded = (int) Math.ceil((double) (totalTicksNeeded * 1000) / totalBurnTime);

        if (available < mbNeeded) {
            double ticksPerMb = (double) totalBurnTime / 1000.0;
            int availableTicks = (int) (available * ticksPerMb);
            itemCount = availableTicks / cookTimeTicks;
            totalTicksNeeded = itemCount * cookTimeTicks;
            mbNeeded = (int) Math.ceil((double) (totalTicksNeeded * 1000) / totalBurnTime);
        }

        if (itemCount <= 0 || mbNeeded <= 0) return;

        double burnTicksPerMb = (double) totalBurnTime / 1000.0;
        int totalFuelTicks = (int) (mbNeeded * burnTicksPerMb);

        handler.drain(mbNeeded, IFluidHandler.FluidAction.EXECUTE);
        entity.litTime = totalFuelTicks;
        entity.litDuration = totalFuelTicks;

        if (!state.getValue(AbstractFurnaceBlock.LIT)) {
            level.setBlock(pos, state.setValue(AbstractFurnaceBlock.LIT, true), 3);
        }
        entity.setChanged();
    }
}