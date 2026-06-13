package com.dadoirie.assortedtweaksnfixes.content.component.furnace_tank;

import net.minecraft.world.item.ItemStack;

public interface FurnaceTankHost {
    ItemStack getItem(int slot);
    void setItem(int slot, ItemStack stack);
    void setChanged();
}