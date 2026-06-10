package com.dadoirie.assortedtweaksnfixes.mixin.createfurnacelavaadapter.brickfurnacelavaadapter;

import de.cech12.brickfurnace.blockentity.AbstractBrickFurnaceBlockEntity;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AbstractBrickFurnaceBlockEntity.class, remap = false)
public interface AbstractBrickFurnaceBlockEntityAccessor {
    @Invoker("canBurn")
    boolean invokeCanBurn(RecipeHolder<?> recipe);
}