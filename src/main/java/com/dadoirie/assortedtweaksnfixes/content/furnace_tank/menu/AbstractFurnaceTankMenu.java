package com.dadoirie.assortedtweaksnfixes.content.furnace_tank.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFurnaceTankMenu extends AbstractFurnaceMenu {
    private int clientLavaStored;

    protected AbstractFurnaceTankMenu(MenuType<?> menuType, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookType recipeBookType, int id, Inventory playerInventory, Container container, ContainerData data) {
        super(menuType, recipeType, recipeBookType, id, playerInventory, container, data);

        this.addSlot(new Slot(container, 3, 25, 19) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                IFluidHandlerItem handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
                return handler != null && handler.getFluidInTank(0).getFluid() == Fluids.LAVA;
            }
        });

        this.addSlot(new Slot(container, 4, 25, 51) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        this.addDataSlot(new net.minecraft.world.inventory.DataSlot() {
            @Override
            public int get() {
                if (container instanceof com.dadoirie.assortedtweaksnfixes.content.furnace_tank.entity.AbstractFurnaceTankBlockEntity be) {
                    return be.getLavaStored();
                }
                return 0;
            }

            @Override
            public void set(int value) {
                clientLavaStored = value;
            }
        });
    }

    public int getLavaStored() {
        return this.clientLavaStored;
    }
}