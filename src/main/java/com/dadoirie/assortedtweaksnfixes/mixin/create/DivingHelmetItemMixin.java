package com.dadoirie.assortedtweaksnfixes.mixin.create;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

/**
 * Create's DivingHelmetItem only ever checks the vanilla HEAD equipment slot for its breathing
 * ability. This wraps that check to also accept the item worn in a Curios slot, without touching
 * the vanilla-slot behavior at all: the original is always called and its result kept whenever
 * it's non-empty, only falling back to a Curios lookup when it isn't.
 */
@Mixin(DivingHelmetItem.class)
public class DivingHelmetItemMixin {

    @WrapMethod(method = "getWornItem")
    private static ItemStack atnf$alsoCheckCurios(Entity entity, Operation<ItemStack> original) {
        ItemStack vanilla = original.call(entity);
        if (!vanilla.isEmpty()) return vanilla;
        if (!(entity instanceof LivingEntity livingEntity)) return vanilla;

        return CuriosApi.getCuriosInventory(livingEntity)
                .flatMap(handler -> handler.findFirstCurio(stack -> stack.getItem() instanceof DivingHelmetItem))
                .map(SlotResult::stack)
                .orElse(vanilla);
    }
}
