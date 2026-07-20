package com.dadoirie.assortedtweaksnfixes.compat.etched;

import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

// RadioBlockEntityMixin keeps the server-side url intact through removal (etched normally nulls it in
// clearContent() before drops are ever computed) - this just copies that surviving value onto the
// matching drop item so placing it back down resumes the same station
public class RadioUrlPreservationFix {

    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBlockEntity() instanceof RadioBlockEntity radio) || radio.getUrl() == null)
            return;

        for (ItemEntity itemEntity : event.getDrops()) {
            ItemStack stack = itemEntity.getItem();
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof RadioBlock) {
                radio.saveToItem(stack, event.getLevel().registryAccess());
                break;
            }
        }
    }
}
