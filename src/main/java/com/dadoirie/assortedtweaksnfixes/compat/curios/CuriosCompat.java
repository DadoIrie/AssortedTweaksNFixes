package com.dadoirie.assortedtweaksnfixes.compat.curios;

import com.possible_triangle.create_jetpack.Content;
import com.simibubi.create.AllItems;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Registers Curios rendering for Create's backtank/jetpack items (back slot) and diving
 * helmet/boots items (head/feet slots). Backtank and jetpack share a single {@link BacktankCurioRenderer};
 * diving helmet and boots each have their own renderer. create_jetpack mandates create as a
 * dependency, so create can be present without create_jetpack but not vice versa; both are still
 * guarded independently here since only one may be loaded.
 */
public class CuriosCompat {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(CuriosCompat::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BacktankCurioRenderer backtankRenderer = new BacktankCurioRenderer();
            DivingHelmetCurioRenderer divingHelmetRenderer = new DivingHelmetCurioRenderer();
            DivingBootsCurioRenderer divingBootsRenderer = new DivingBootsCurioRenderer();

            if (ModList.get().isLoaded("create")) {
                registerItem(AllItems.COPPER_BACKTANK.get(), backtankRenderer);
                registerItem(AllItems.NETHERITE_BACKTANK.get(), backtankRenderer);
                registerItem(AllItems.COPPER_DIVING_HELMET.get(), divingHelmetRenderer);
                registerItem(AllItems.NETHERITE_DIVING_HELMET.get(), divingHelmetRenderer);
                registerItem(AllItems.COPPER_DIVING_BOOTS.get(), divingBootsRenderer);
                registerItem(AllItems.NETHERITE_DIVING_BOOTS.get(), divingBootsRenderer);
            }
            if (ModList.get().isLoaded("create_jetpack")) {
                registerItem(Content.INSTANCE.getJETPACK_ITEM().get(), backtankRenderer);
                registerItem(Content.INSTANCE.getNETHERITE_JETPACK_ITEM().get(), backtankRenderer);
            }
        });
    }

    private static void registerItem(Item item, ICurioRenderer renderer) {
        CuriosRendererRegistry.register(item, () -> renderer);
    }
}
