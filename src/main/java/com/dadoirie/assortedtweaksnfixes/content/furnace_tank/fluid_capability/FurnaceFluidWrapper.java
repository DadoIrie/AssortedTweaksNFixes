package com.dadoirie.assortedtweaksnfixes.content.furnace_tank.fluid_capability;

import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.entity.AbstractFurnaceTankBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FurnaceFluidWrapper implements IFluidHandler {

    private final AbstractFurnaceTankBlockEntity furnace;

    public FurnaceFluidWrapper(AbstractFurnaceTankBlockEntity furnace) {
        this.furnace = furnace;
    }

    @Override
    public int getTanks() { return 1; }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank != 0) return FluidStack.EMPTY;
        int stored = furnace.getLavaStored();
        return stored > 0 ? new FluidStack(Fluids.LAVA, stored) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return AbstractFurnaceTankBlockEntity.CAPACITY;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tank == 0 && stack.getFluid() == Fluids.LAVA;
    }

    @Override
    public int fill(FluidStack resource, @NotNull FluidAction action) {
        if (resource.isEmpty() || resource.getFluid() != Fluids.LAVA) return 0;
        int space = AbstractFurnaceTankBlockEntity.CAPACITY - furnace.getLavaStored();
        if (space <= 0) return 0;
        int filled = Math.min(resource.getAmount(), space);
        if (action.execute()) furnace.setLavaStored(furnace.getLavaStored() + filled);
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        if (resource.isEmpty() || resource.getFluid() != Fluids.LAVA) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        int stored = furnace.getLavaStored();
        if (stored <= 0) return FluidStack.EMPTY;
        int drained = Math.min(stored, maxDrain);
        if (action.execute()) furnace.setLavaStored(stored - drained);
        return new FluidStack(Fluids.LAVA, drained);
    }
}