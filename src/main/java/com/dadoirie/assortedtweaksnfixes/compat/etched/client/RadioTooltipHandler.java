package com.dadoirie.assortedtweaksnfixes.compat.etched.client;

import gg.moonflower.etched.common.block.RadioBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class RadioTooltipHandler {

    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof RadioBlock))
            return;

        CustomData data = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();
        if (!tag.contains("Url", Tag.TAG_STRING))
            return;

        String url = tag.getString("Url");
        if (!url.isEmpty()) {
            event.getToolTip().add(Component.literal(url).withStyle(ChatFormatting.GREEN));
        }
    }
}
