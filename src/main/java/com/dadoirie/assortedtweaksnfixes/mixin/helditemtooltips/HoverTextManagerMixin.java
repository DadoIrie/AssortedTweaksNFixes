package com.dadoirie.assortedtweaksnfixes.mixin.helditemtooltips;

import dev.chililisoup.condiments.block.entity.CrateContents;
import dev.chililisoup.condiments.item.CrateItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(fuzs.helditemtooltips.client.gui.screens.inventory.tooltip.HoverTextManager.class)
public class HoverTextManagerMixin {

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private static void modifyCrateItemName(ItemStack itemStack, Level level, int maxLines, CallbackInfoReturnable<List<Component>> cir) {
        if (itemStack.getItem() instanceof CrateItem) {
            CrateContents crateContents = CrateContents.fromCrateItem(itemStack);
            Optional<ItemStack> containedItem = crateContents.item();
            
            if (containedItem.isPresent() && !containedItem.get().is(Items.AIR)) {
                List<Component> tooltipLines = cir.getReturnValue();
                if (!tooltipLines.isEmpty()) {
                    Component originalName = tooltipLines.getFirst();
                    MutableComponent modifiedName = Component.literal(originalName.getString() + " (" + containedItem.get().getHoverName().getString() + ")");
                    tooltipLines.set(0, modifiedName);
                }
            }
        }
    }
}
