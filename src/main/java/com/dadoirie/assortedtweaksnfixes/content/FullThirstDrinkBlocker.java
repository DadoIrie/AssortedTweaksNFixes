package com.dadoirie.assortedtweaksnfixes.content;

import cn.mlus.thirst.api.ThirstHelper;
import cn.mlus.thirst.foundation.common.capability.ModAttachment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

public class FullThirstDrinkBlocker {

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (player.isCreative() || player.isSpectator())
            return;

        ItemStack stack = event.getItem();
        if (!ThirstHelper.isDrink(stack) && !(stack.getItem() instanceof PotionItem))
            return;

        if (player.getData(ModAttachment.PLAYER_THIRST).getThirst() >= 20) {
            event.setCanceled(true);
            if (!player.level().isClientSide())
                player.displayClientMessage(Component.translatable("atnf.thirst.not_thirsty"), true);
        }
    }
}