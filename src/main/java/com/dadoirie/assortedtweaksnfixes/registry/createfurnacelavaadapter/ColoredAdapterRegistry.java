package com.dadoirie.assortedtweaksnfixes.registry.createfurnacelavaadapter;

import net.mcreator.createfurnacelavaadapter.block.FurnaceLavaAdapterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ColoredAdapterRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("assortedtweaksnfixes");
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("assortedtweaksnfixes");

    public static final String[] COLORS = {
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    public static void register(IEventBus bus, DeferredHolder<CreativeModeTab, CreativeModeTab> tab) {
        for (String color : COLORS) {
            DeferredBlock<Block> block = BLOCKS.register(color + "_furnace_lava_adapter", FurnaceLavaAdapterBlock::new);
            ITEMS.register(color + "_furnace_lava_adapter", () -> new BlockItem(block.get(), new Item.Properties()));
        }
        BLOCKS.register(bus);
        ITEMS.register(bus);

        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() == tab.getKey()) {
                ITEMS.getEntries().forEach(holder -> event.accept(holder.get()));
            }
        });
    }
}