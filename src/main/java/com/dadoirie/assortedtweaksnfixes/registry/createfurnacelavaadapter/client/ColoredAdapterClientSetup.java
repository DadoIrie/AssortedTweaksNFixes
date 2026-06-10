package com.dadoirie.assortedtweaksnfixes.registry.createfurnacelavaadapter.client;

import com.dadoirie.assortedtweaksnfixes.registry.createfurnacelavaadapter.ColoredAdapterRegistry;
import net.mcreator.createfurnacelavaadapter.FurnaceLavaAdapterEntityRenderer;
import net.mcreator.createfurnacelavaadapter.init.CreateFurnaceLavaAdapterModBlockEntities;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ColoredAdapterClientSetup {
    public static void init(net.neoforged.bus.api.IEventBus modEventBus) {
        modEventBus.addListener(ColoredAdapterClientSetup::registerBlockColors);
        modEventBus.addListener(ColoredAdapterClientSetup::registerItemColors);
        modEventBus.addListener(ColoredAdapterClientSetup::registerRenderers);
    }

    private static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        ColoredAdapterRegistry.BLOCKS.getEntries().forEach(holder -> {
            String path = holder.getId().getPath();
            String color = path.substring(0, path.indexOf("_furnace_lava_adapter"));
            int hexColor = DyeColor.byName(color, DyeColor.WHITE).getTextureDiffuseColor();
            event.register((state, level, pos, tintIndex) -> hexColor, holder.get());
        });
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        ColoredAdapterRegistry.ITEMS.getEntries().forEach(holder -> {
            String path = holder.getId().getPath();
            String color = path.substring(0, path.indexOf("_furnace_lava_adapter"));
            int hexColor = DyeColor.byName(color, DyeColor.WHITE).getTextureDiffuseColor();
            event.register((stack, tintIndex) -> hexColor, holder.get());
        });
    }

    // New method to link the renderer to your block entity
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                CreateFurnaceLavaAdapterModBlockEntities.FURNACE_LAVA_ADAPTER.get(),
                FurnaceLavaAdapterEntityRenderer::new
        );
    }
}