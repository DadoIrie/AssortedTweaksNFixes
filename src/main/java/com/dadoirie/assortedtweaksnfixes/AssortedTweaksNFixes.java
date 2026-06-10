package com.dadoirie.assortedtweaksnfixes;

import com.dadoirie.assortedtweaksnfixes.compat.mekanism.DyeDepotCompat;
import com.dadoirie.assortedtweaksnfixes.compat.yigd.DeathCharmCompat;
import com.dadoirie.assortedtweaksnfixes.registry.createfurnacelavaadapter.ColoredAdapterRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.player005.recipe_modification.serialization.RecipeModifierManager;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(AssortedTweaksNFixesConstants.MOD_ID)
public class AssortedTweaksNFixes {
    private static final boolean IS_DEDICATED_SERVER = FMLEnvironment.dist.isDedicatedServer();
    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(AssortedTweaksNFixes.class);

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AssortedTweaksNFixesConstants.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register("main_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + AssortedTweaksNFixesConstants.MOD_ID))
            .icon(() -> new ItemStack(Items.LAVA_BUCKET))
            .build());

    public AssortedTweaksNFixes(IEventBus modEventBus) {
        LOGGER.info("AssortedTweaksNFixes loaded on {}", IS_DEDICATED_SERVER ? "dedicated server" : "client");

        CREATIVE_TABS.register(modEventBus);

        if (ModList.get().isLoaded("create_furnace_lava_adapter") && ModList.get().isLoaded("colorfulpipes")) {
            ColoredAdapterRegistry.register(modEventBus, MAIN_TAB);
        }

        if (ModList.get().isLoaded("mekanism") && ModList.get().isLoaded("dye_depot")) {
            if (!ModList.get().isLoaded("recipe_modification")) {
                throw new IllegalStateException("Recipe modification mod is required for the Mekanism and Dye Depot compat.");
            }
            DyeDepotCompat.register(modEventBus);
            NeoForge.EVENT_BUS.addListener(AssortedTweaksNFixes::onAddReloadListeners);
        }
        if (ModList.get().isLoaded("everycomp") || ModList.get().isLoaded("stonezone")) {
            modEventBus.addListener(AssortedTweaksNFixes::onRegisterCapabilities);
        }
        DeathCharmCompat.init();
    }

    static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        if (ModList.get().isLoaded("everycomp")) {
            registerCompatChestCapabilities(event,
                    "net.mehvahdjukaar.every_compat.common_classes.CompatChestBlock",
                    "net.mehvahdjukaar.every_compat.common_classes.CompatTrappedChestBlock"
            );
        }
        if (ModList.get().isLoaded("stonezone")) {
            registerCompatChestCapabilities(event,
                    "net.mehvahdjukaar.stone_zone.common_classes.CompatChestBlock",
                    "net.mehvahdjukaar.stone_zone.common_classes.CompatTrappedChestBlock"
            );
        }
    }

    private static void registerCompatChestCapabilities(RegisterCapabilitiesEvent event, String... classNames) {
        List<Class<?>> chestClasses = new ArrayList<>();
        for (String className : classNames) {
            try {
                chestClasses.add(Class.forName(className));
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Could not find class {}, skipping capability registration", className);
            }
        }
        if (chestClasses.isEmpty()) return;

        for (Block block : BuiltInRegistries.BLOCK) {
            if (chestClasses.stream().anyMatch(c -> c.isInstance(block))) {
                LOGGER.info("Registering IItemHandler capability for chest: {}", BuiltInRegistries.BLOCK.getKey(block));
                event.registerBlock(
                        Capabilities.ItemHandler.BLOCK,
                        (level, pos, state, be, side) -> {
                            if (be instanceof ChestBlockEntity chest) {
                                return new InvWrapper(chest);
                            }
                            return null;
                        },
                        block
                );
            }
        }
    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new RecipeModifierManager());
    }
}