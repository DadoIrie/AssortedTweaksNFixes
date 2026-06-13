package com.dadoirie.assortedtweaksnfixes.content.furnace_tank;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FurnaceItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, AssortedTweaksNFixesConstants.MOD_ID);

    public static final DeferredHolder<Item, BlockItem> FURNACE_TANK = ITEMS.register("furnace_tank",
            () -> new BlockItem(FurnaceTankRegistry.FURNACE_TANK.get(), new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> BLAST_FURNACE_TANK = ITEMS.register("blast_furnace_tank",
            () -> new BlockItem(FurnaceTankRegistry.BLAST_FURNACE_TANK.get(), new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> SMOKER_TANK = ITEMS.register("smoker_tank",
            () -> new BlockItem(FurnaceTankRegistry.SMOKER_TANK.get(), new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}