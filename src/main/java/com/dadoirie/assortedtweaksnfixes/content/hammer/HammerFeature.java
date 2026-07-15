package com.dadoirie.assortedtweaksnfixes.content.hammer;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.content.ATNFCreativeTabs;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemComponent;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class HammerFeature {

    private static final ResourceLocation HAMMERING_ID = ATNFConstants.identifer("hammering");

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ATNFConstants.MOD_ID);
    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ATNFConstants.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ATNFConstants.MOD_ID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ATNFConstants.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SandPaperItemComponent>> PROCESSING_ITEM =
            DATA_COMPONENTS.registerComponentType("processing_item",
                    builder -> builder.persistent(SandPaperItemComponent.CODEC).networkSynchronized(SandPaperItemComponent.STREAM_CODEC));

    private static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> HAMMERING_SERIALIZER =
            RECIPE_SERIALIZERS.register("hammering", () -> new StandardProcessingRecipe.Serializer<>(HammerRecipe::new));

    private static final DeferredHolder<RecipeType<?>, RecipeType<?>> HAMMERING_TYPE =
            RECIPE_TYPES.register("hammering", () -> RecipeType.simple(HAMMERING_ID));

    public static final IRecipeTypeInfo HAMMERING = new IRecipeTypeInfo() {
        @Override
        public ResourceLocation getId() {
            return HAMMERING_ID;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) HAMMERING_SERIALIZER.get();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
            return (RecipeType<R>) HAMMERING_TYPE.get();
        }
    };

    public static final DeferredItem<HammerItem> HAMMER =
            ITEMS.registerItem("hammer", HammerItem::new, new Item.Properties().durability(128));

    private HammerFeature() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        modEventBus.addListener(HammerFeature::addToCreativeTab);
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ATNFCreativeTabs.MAIN.getKey()) {
            event.accept(HAMMER.get());
        }
    }
}