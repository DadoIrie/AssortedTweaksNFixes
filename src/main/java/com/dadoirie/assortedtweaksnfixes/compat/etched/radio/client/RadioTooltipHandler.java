package com.dadoirie.assortedtweaksnfixes.compat.etched.radio.client;

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

        String displayValue = null;
        if (tag.contains("Name", Tag.TAG_STRING)) {
            String name = tag.getString("Name");
            if (!name.isEmpty()) {
                displayValue = name;
            }
        }

        if (displayValue == null && tag.contains("Url", Tag.TAG_STRING)) {
            String url = tag.getString("Url");
            if (!url.isEmpty()) {
                displayValue = url;
            }
        }

        if (displayValue != null) {
            event.getToolTip().add(Component.literal(displayValue).withStyle(ChatFormatting.DARK_GREEN));
        }
    }
}