package com.dadoirie.assortedtweaksnfixes.mixin.createfurnacelavaadapter.brickfurnacelavaadapter;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    @Invoker("getBurnDuration")
    int invokeGetBurnDuration(ItemStack stack);
}