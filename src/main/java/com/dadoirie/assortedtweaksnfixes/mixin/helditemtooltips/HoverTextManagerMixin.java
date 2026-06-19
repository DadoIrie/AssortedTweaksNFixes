package com.dadoirie.assortedtweaksnfixes.mixin.helditemtooltips;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.chililisoup.condiments.block.entity.CrateContents;
import dev.chililisoup.condiments.item.CrateItem;
import fuzs.helditemtooltips.client.gui.screens.inventory.tooltip.HoverTextManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;

@Mixin(HoverTextManager.class)
public abstract class HoverTextManagerMixin {

    @ModifyReturnValue(
            method = "getTooltipLines",
            at = @At("RETURN")
    )
    private static List<Component> assortedtweaksnfixes$modifyCrateTooltip(List<Component> original, ItemStack itemStack, Level level, int maxLines) {
        if (itemStack.getItem() instanceof CrateItem && original.size() >= 2) {
            CrateContents crateContents = CrateContents.fromCrateItem(itemStack);
            Optional<ItemStack> contained = crateContents.item();
            if (contained.isPresent() && !contained.get().is(Items.AIR)) {
                original.set(1, original.get(1).copy().append(" ").append(contained.get().getHoverName().copy().withStyle(ChatFormatting.WHITE)));
            }
        }
        return original;
    }
}