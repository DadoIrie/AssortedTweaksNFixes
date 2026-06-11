package com.dadoirie.assortedtweaksnfixes.mixin.minecraft;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    @Invoker("getBurnDuration")
    int invokeGetBurnDuration(ItemStack fuel);

    @Invoker("canBurn")
    static boolean invokeCanBurn(RegistryAccess registryAccess, RecipeHolder<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize, AbstractFurnaceBlockEntity furnace) {
        throw new UnsupportedOperationException();
    }

    @Accessor("cookingTotalTime")
    int getCookingTotalTime();

    @Accessor("litTime")
    void setLitTime(int litTime);

    @Accessor("litDuration")
    void setLitDuration(int litDuration);
}