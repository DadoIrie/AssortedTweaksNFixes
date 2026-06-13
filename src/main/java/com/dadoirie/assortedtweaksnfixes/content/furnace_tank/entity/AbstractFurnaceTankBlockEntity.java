package com.dadoirie.assortedtweaksnfixes.content.furnace_tank.entity;

import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.AbstractFurnaceBlockEntityAccessor;
import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.fluid_capability.FluidFuelHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public abstract class AbstractFurnaceTankBlockEntity extends AbstractFurnaceBlockEntity {

    private static final String LAVA_KEY = "lava_stored";
    public static final int CAPACITY = 1000;
    private int lavaStored = 0;

    public AbstractFurnaceTankBlockEntity(BlockEntityType<?> type, BlockPos pos,
                                          BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(type, pos, state, recipeType);
        this.items = net.minecraft.core.NonNullList.withSize(5, ItemStack.EMPTY);
    }

    public int getLavaStored() {
        return lavaStored;
    }

    public void setLavaStored(int amount) {
        this.lavaStored = Math.clamp(amount, 0, CAPACITY);
        setChanged();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(LAVA_KEY, lavaStored);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        lavaStored = tag.getInt(LAVA_KEY);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt(LAVA_KEY, lavaStored);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.handleUpdateTag(tag, registries);
        lavaStored = tag.getInt(LAVA_KEY);
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractFurnaceTankBlockEntity be) {
        be.processFuelSlotItem();
        AbstractFurnaceBlockEntity.serverTick(level, pos, state, be);
        FluidFuelHandler.tick(level, pos, state, be);
    }

    public void processFuelSlotItem() {
        ItemStack inputStack = this.getItem(3);
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
        ItemStack outputStack = this.getItem(4);

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
                this.setItem(4, emptyContainer);
            } else {
                outputStack.grow(emptyContainer.getCount());
            }
        }
        this.setChanged();
    }

    public int getTankCookingTotalTime() {
        return ((AbstractFurnaceBlockEntityAccessor) this).getCookingTotalTime();
    }

    public void setTankLitTime(int time) {
        ((AbstractFurnaceBlockEntityAccessor) this).setLitTime(time);
    }

    public void setTankLitDuration(int duration) {
        ((AbstractFurnaceBlockEntityAccessor) this).setLitDuration(duration);
    }
}