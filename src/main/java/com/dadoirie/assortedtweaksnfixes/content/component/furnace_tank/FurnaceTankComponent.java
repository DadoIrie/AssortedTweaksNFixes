package com.dadoirie.assortedtweaksnfixes.content.component.furnace_tank;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FurnaceTankComponent {
    private static final String LAVA_KEY = "lava_stored";
    public static final int CAPACITY = 1000;
    private int lavaStored = 0;
    private final FurnaceTankHost host;

    public FurnaceTankComponent(FurnaceTankHost host) {
        this.host = host;
    }

    public int getLavaStored() {
        return lavaStored;
    }

    public void setLavaStored(int amount) {
        this.lavaStored = Math.clamp(amount, 0, CAPACITY);
        host.setChanged();
    }

    public void save(CompoundTag tag) {
        tag.putInt(LAVA_KEY, lavaStored);
    }

    public void load(CompoundTag tag) {
        lavaStored = tag.getInt(LAVA_KEY);
    }

    public void processFuelSlotItem() {
        ItemStack inputStack = host.getItem(3);
        if (inputStack.isEmpty()) return;

        ItemStack singleCopy = inputStack.copyWithCount(1);
        var itemHandler = singleCopy.getCapability(Capabilities.FluidHandler.ITEM);
        if (itemHandler == null) return;

        FluidStack simulated = itemHandler.drain(CAPACITY, IFluidHandler.FluidAction.SIMULATE);
        if (simulated.isEmpty() || simulated.getFluid() != Fluids.LAVA) return;

        int space = CAPACITY - this.lavaStored;
        if (space < simulated.getAmount()) return;

        FluidStack drained = itemHandler.drain(simulated.getAmount(), IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) return;

        ItemStack emptyContainer = itemHandler.getContainer();
        ItemStack outputStack = host.getItem(4);

        if (!emptyContainer.isEmpty()) {
            if (!outputStack.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(outputStack, emptyContainer)) return;
                if (outputStack.getCount() + emptyContainer.getCount() > outputStack.getMaxStackSize()) return;
            }
        }

        this.setLavaStored(this.lavaStored + drained.getAmount());
        inputStack.shrink(1);

        if (!emptyContainer.isEmpty()) {
            if (outputStack.isEmpty()) {
                host.setItem(4, emptyContainer);
            } else {
                outputStack.grow(emptyContainer.getCount());
            }
        }
        host.setChanged();
    }
}