package com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.fluid_capability;

import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.entity.AbstractFurnaceTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidFuelHandler {

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractFurnaceTankBlockEntity be) {
        if (be.litTime > 0) return;

        ItemStack input = be.getItem(0);
        if (input.isEmpty()) return;

        var recipe = be.quickCheck.getRecipeFor(new SingleRecipeInput(input), level).orElse(null);
        if (recipe == null) return;

        ItemStack output = be.getItem(2);
        if (!output.isEmpty()) {
            ItemStack recipeOutput = recipe.value().getResultItem(level.registryAccess());
            if (!ItemStack.isSameItemSameComponents(output, recipeOutput) ||
                    output.getCount() + recipeOutput.getCount() > output.getMaxStackSize()) return;
        }

        IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos, state, be, null);
        if (handler == null) return;

        int fuelAvailable = handler.getFluidInTank(0).getAmount();
        if (fuelAvailable <= 0) return;

        int cookTimeTicks = be.getTankCookingTotalTime();
        int totalBurnTime = 20000;
        int itemCount = Math.min(input.getCount(), 64);

        int totalTicksNeeded = itemCount * cookTimeTicks;
        int mbNeeded = (int) Math.ceil((double) (totalTicksNeeded * 1000) / totalBurnTime);

        if (fuelAvailable < mbNeeded) {
            double ticksPerMb = (double) totalBurnTime / 1000.0;
            int availableTicks = (int) (fuelAvailable * ticksPerMb);
            itemCount = availableTicks / cookTimeTicks;
            totalTicksNeeded = itemCount * cookTimeTicks;
            mbNeeded = (int) Math.ceil((double) (totalTicksNeeded * 1000) / totalBurnTime);
        }

        if (itemCount <= 0 || mbNeeded <= 0) return;

        double burnTicksPerMb = (double) totalBurnTime / 1000.0;
        int totalFuelTicks = (int) (mbNeeded * burnTicksPerMb);

        handler.drain(mbNeeded, IFluidHandler.FluidAction.EXECUTE);
        be.setTankLitTime(totalFuelTicks + 1);
        be.setTankLitDuration(totalFuelTicks + 1);

        if (!state.getValue(AbstractFurnaceBlock.LIT)) {
            level.setBlock(pos, state.setValue(AbstractFurnaceBlock.LIT, true), 3);
        }
        be.setChanged();
    }
}