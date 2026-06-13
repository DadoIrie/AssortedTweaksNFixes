package com.dadoirie.assortedtweaksnfixes.content.furnace_tank.entity;

import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.AbstractFurnaceBlockEntityAccessor;
import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.fluid_capability.FluidFuelHandler;
import com.dadoirie.assortedtweaksnfixes.content.component.furnace_tank.FurnaceTankComponent;
import com.dadoirie.assortedtweaksnfixes.content.component.furnace_tank.FurnaceTankHost;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFurnaceTankBlockEntity extends AbstractFurnaceBlockEntity implements FurnaceTankHost {

    protected final FurnaceTankComponent tankComponent;

    public AbstractFurnaceTankBlockEntity(BlockEntityType<?> type, BlockPos pos,
                                          BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(type, pos, state, recipeType);
        this.items = net.minecraft.core.NonNullList.withSize(5, ItemStack.EMPTY);
        this.tankComponent = new FurnaceTankComponent(this);
    }

    public int getLavaStored() {
        return tankComponent.getLavaStored();
    }

    public void setLavaStored(int amount) {
        tankComponent.setLavaStored(amount);
    }

    public FurnaceTankComponent getTankComponent() {
        return this.tankComponent;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tankComponent.save(tag);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        tankComponent.load(tag);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tankComponent.save(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.handleUpdateTag(tag, registries);
        tankComponent.load(tag);
    }

    @Override
    public int getContainerSize() {
        return 5;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractFurnaceTankBlockEntity be) {
        be.tankComponent.processFuelSlotItem();
        AbstractFurnaceBlockEntity.serverTick(level, pos, state, be);
        FluidFuelHandler.tick(level, pos, state, be);
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