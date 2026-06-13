package com.dadoirie.assortedtweaksnfixes.content.furnace_tank.entity;

import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.FurnaceTankRegistry;
import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.menu.BlastFurnaceTankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlastFurnaceTankBlockEntity extends AbstractFurnaceTankBlockEntity {
    public BlastFurnaceTankBlockEntity(BlockPos pos, BlockState state) {
        super(FurnaceTankRegistry.BLAST_FURNACE_TANK_ENTITY.get(), pos, state, RecipeType.BLASTING);
    }

    @Override
    @NotNull
    protected Component getDefaultName() {
        return Component.translatable("assortedtweaksnfixes.tank_furnace.blast_furnace");
    }

    @Override
    @NotNull
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new BlastFurnaceTankMenu(id, player, this, this.dataAccess);
    }
}