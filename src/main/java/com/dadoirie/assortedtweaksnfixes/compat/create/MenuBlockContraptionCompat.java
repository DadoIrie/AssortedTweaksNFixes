package com.dadoirie.assortedtweaksnfixes.compat.create;

import com.dadoirie.assortedtweaksnfixes.compat.create.contraption.MenuBlockInteractionBehaviour;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class MenuBlockContraptionCompat {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(MenuBlockContraptionCompat::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            boolean fletchingRecipeLoaded = ModList.get().isLoaded("fletchingrecipe");
            for (Block block : BuiltInRegistries.BLOCK) {
                MenuBlockInteractionBehaviour behaviour = MenuBlockInteractionBehaviour.forBlock(block, fletchingRecipeLoaded);
                if (behaviour != null) {
                    MovingInteractionBehaviour.REGISTRY.register(block, behaviour);
                }
            }
        });
    }
}
