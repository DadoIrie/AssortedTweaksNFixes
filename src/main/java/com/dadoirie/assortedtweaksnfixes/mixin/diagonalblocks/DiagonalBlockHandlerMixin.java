package com.dadoirie.assortedtweaksnfixes.mixin.diagonalblocks;

import com.google.common.collect.BiMap;
import fuzs.diagonalblocks.api.v2.DiagonalBlockType;
import fuzs.diagonalblocks.handler.DiagonalBlockHandler;
import fuzs.puzzleslib.api.block.v1.BlockConversionHelper;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(DiagonalBlockHandler.class)
public class DiagonalBlockHandlerMixin {

    @Inject(
            method = "onTagsUpdated",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injectOnTagsUpdated(RegistryAccess registryAccess, boolean client, CallbackInfo ci) {

        // First loop unchanged
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (entry.getValue() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                setItemForBlock(entry.getKey().location(), blockItem, block);
                setBlockForItem(blockItem, block);
            }
        }

        // Replacement second loop exactly as you wrote it
        for (DiagonalBlockType type : DiagonalBlockType.TYPES) {
            type.getBlockConversions().forEach((from, to) -> {
                try {
                    BlockConversionHelper.copyBoundTags(from, to);
                } catch (IllegalStateException e) {
                    // Merge tags via reflection
                    try {
                        Set<TagKey<Block>> fromTags = from.builtInRegistryHolder().tags().collect(Collectors.toSet());
                        Set<TagKey<Block>> toTags = to.builtInRegistryHolder().tags().collect(Collectors.toSet());
                        Set<TagKey<Block>> merged = new java.util.HashSet<>(toTags);
                        merged.addAll(fromTags);

                        java.lang.reflect.Method bindTags = to.builtInRegistryHolder().getClass()
                                .getDeclaredMethod("bindTags", java.util.Collection.class);
                        bindTags.setAccessible(true);
                        bindTags.invoke(to.builtInRegistryHolder(), merged);
                    } catch (ReflectiveOperationException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        ci.cancel(); // skip the original method entirely
    }

    private static void setItemForBlock(ResourceLocation resourceLocation, BlockItem blockItem, Block block) {
        for (DiagonalBlockType type : DiagonalBlockType.TYPES) {
            // item id should be fine to use for block items
            if (type.isTarget(resourceLocation, block)) {
                BlockConversionHelper.setItemForBlock(type.getBlockConversions().get(block), blockItem);
                break;
            }
        }
    }

    private static void setBlockForItem(BlockItem blockItem, Block block) {
        for (DiagonalBlockType type : DiagonalBlockType.TYPES) {
            BiMap<Block, Block> conversions = type.getBlockConversions();
            Block baseBlock;
            Block diagonalBlock = conversions.get(block);
            if (diagonalBlock != null) {
                baseBlock = block;
            } else {
                baseBlock = conversions.inverse().get(block);
                if (baseBlock != null) {
                    diagonalBlock = block;
                } else {
                    continue;
                }
            }
            if (RegistryHelper.is(type.getBlacklistTagKey(), baseBlock)) {
                BlockConversionHelper.setBlockForItem(blockItem, baseBlock);
            } else {
                BlockConversionHelper.setBlockForItem(blockItem, diagonalBlock);
            }
            break;
        }
    }
}