package com.dadoirie.assortedtweaksnfixes.mixin.brickfurnace.abstractbrickfurnaceblockentity;

import de.cech12.brickfurnace.blockentity.AbstractBrickFurnaceBlockEntity;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractBrickFurnaceBlockEntity.class)
public interface AbstractBrickFurnaceBlockEntityAccessor {
    @Invoker("getTotalCookTime")
    int invokeGetTotalCookTime(RecipeHolder<? extends AbstractCookingRecipe> rec);
}