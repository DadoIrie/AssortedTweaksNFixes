package com.dadoirie.assortedtweaksnfixes.mixin.additionalplacements;

import com.firemerald.additionalplacements.client.ClientModEvents;
import dev.chililisoup.condiments.block.entity.CrateContents;
import dev.chililisoup.condiments.item.CrateItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(ClientModEvents.class)
public abstract class ClientModEventsMixin {

    @ModifyVariable(
            method = "onHighlightBlock",
            at = @At(
                    value = "STORE",
                    ordinal = 0,
                    target = "Lnet/minecraft/world/item/ItemStack;<init>()V"
            ),
            name = "stack"
    )
    private static ItemStack assortedtweaksnfixes$unwrapCrateForHighlight(ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }

        if (stack.getItem() instanceof CrateItem) {
            CrateContents contents = CrateContents.fromCrateItem(stack);

            Optional<ItemStack> contained = contents.item();
            if (contained.isPresent() && !contained.get().is(Items.AIR) && contents.count() > 0) {
                return contained.get();
            }
        }

        return stack;
    }
}