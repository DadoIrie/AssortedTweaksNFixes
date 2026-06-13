package com.dadoirie.assortedtweaksnfixes.content.furnace_tank.menu;

import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.FurnaceTankRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class FurnaceTankMenu extends AbstractFurnaceTankMenu {
    public FurnaceTankMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(5), new SimpleContainerData(4));
    }

    public FurnaceTankMenu(int id, Inventory playerInventory, Container container, ContainerData data) {
        super(FurnaceTankRegistry.FURNACE_TANK_MENU.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, id, playerInventory, container, data);
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return FurnaceTankRegistry.FURNACE_TANK_MENU.get();
    }
}