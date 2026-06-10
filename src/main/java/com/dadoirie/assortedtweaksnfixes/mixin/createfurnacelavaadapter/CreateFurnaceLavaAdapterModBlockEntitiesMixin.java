package com.dadoirie.assortedtweaksnfixes.mixin.createfurnacelavaadapter;

import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.BlockEntityTypeAccessor;
import com.dadoirie.assortedtweaksnfixes.registry.createfurnacelavaadapter.ColoredAdapterRegistry;
import net.mcreator.createfurnacelavaadapter.init.CreateFurnaceLavaAdapterModBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = CreateFurnaceLavaAdapterModBlockEntities.class, remap = false)
public class CreateFurnaceLavaAdapterModBlockEntitiesMixin {

    @Inject(method = "registerCapabilities", at = @At("HEAD"))
    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event, CallbackInfo ci) {
        BlockEntityType<?> type = CreateFurnaceLavaAdapterModBlockEntities.FURNACE_LAVA_ADAPTER.get();
        BlockEntityTypeAccessor accessor = (BlockEntityTypeAccessor) type;

        Set<Block> validBlocks = new HashSet<>(accessor.getValidBlocks());
        ColoredAdapterRegistry.BLOCKS.getEntries().forEach(holder -> validBlocks.add(holder.get()));

        accessor.setValidBlocks(validBlocks);
    }
}