package com.dadoirie.assortedtweaksnfixes.tweaks.fluid;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FurnaceFluidWrapper implements IFluidHandler {

    private static final String TAG_KEY = "assortedtweaks_lava_mb";
    private static final int CAPACITY = 1000;

    private final AbstractFurnaceBlockEntity furnace;

    public FurnaceFluidWrapper(AbstractFurnaceBlockEntity furnace) {
        this.furnace = furnace;
    }

    private int getStored() {
        return furnace.getPersistentData().getInt(TAG_KEY);
    }

    private void setStored(int amount) {
        furnace.getPersistentData().putInt(TAG_KEY, Math.max(0, amount));
        furnace.setChanged();
    }

    @Override
    public int getTanks() { return 1; }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank != 0) return FluidStack.EMPTY;
        int stored = getStored();
        return stored > 0 ? new FluidStack(Fluids.LAVA, stored) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) { return CAPACITY; }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tank == 0 && stack.getFluid() == Fluids.LAVA;
    }

    @Override
    public int fill(FluidStack resource, @NotNull FluidAction action) {
        if (resource.isEmpty() || resource.getFluid() != Fluids.LAVA) return 0;
        int current = getStored();
        int space = CAPACITY - current;
        if (space <= 0) return 0;
        int filled = Math.min(resource.getAmount(), space);
        if (action.execute()) {
            setStored(current + filled);
        }
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        if (resource.isEmpty() || resource.getFluid() != Fluids.LAVA) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        int stored = getStored();
        if (stored <= 0) return FluidStack.EMPTY;
        int drained = Math.min(stored, maxDrain);
        if (action.execute()) {
            setStored(stored - drained);
        }
        return new FluidStack(Fluids.LAVA, drained);
    }
}