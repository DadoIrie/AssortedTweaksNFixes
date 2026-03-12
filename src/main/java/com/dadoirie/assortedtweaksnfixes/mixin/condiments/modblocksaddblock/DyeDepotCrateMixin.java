package com.dadoirie.assortedtweaksnfixes.mixin.condiments.modblocksaddblock;

import com.dadoirie.assortedtweaksnfixes.mixin.condiments.ModBlocksAddBlockMixin;
import com.dadoirie.assortedtweaksnfixes.compat.condiments.DyeDepotCrateRegistry;
import dev.chililisoup.condiments.block.entity.CrateContents;
import dev.chililisoup.condiments.item.CrateItem;
import dev.chililisoup.condiments.reg.ModBlocks;
import dev.chililisoup.condiments.block.CrateBlock;
import dev.chililisoup.condiments.reg.ModComponents;
import com.ninni.dye_depot.registry.DDDyes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Supplier;

@Mixin(ModBlocks.class)
public class DyeDepotCrateMixin {

    @Inject(method = "init", at = @At("RETURN"))
    private static void injectDyeDepotCrates(CallbackInfo ci) {
        for (DDDyes dye : DDDyes.values()) {
            if (!DDDyes.isModDye(dye.get())) continue;

            String name = dye.getName() + "_crate";

            Supplier<Block> crateSupplier = ModBlocksAddBlockMixin.invokeAddBlock(
                    new ModBlocks.Params(name, () -> new CrateBlock(dye.get(),
                            BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                                    .pushReaction(PushReaction.DESTROY))) {
                        @Override
                        public BlockItem getItem(Supplier<? extends Block> block) {
                            return new CrateItem(block.get(),
                                    (new Item.Properties())
                                            .component(ModComponents.CRATE_CONTENTS.get(), CrateContents.EMPTY));
                        }
                    }
            );

            DyeDepotCrateRegistry.DYE_CRATES.put(dye.get(), crateSupplier);
        }
    }

    @Inject(method = "getCrates", at = @At("RETURN"), cancellable = true)
    private static void injectAddDyeDepotCrates(CallbackInfoReturnable<Block[]> cir) {
        Block[] original = cir.getReturnValue();

        List<Block> combined = new ArrayList<>(original.length + DyeDepotCrateRegistry.DYE_CRATES.size());
        combined.addAll(Arrays.asList(original));

        DyeDepotCrateRegistry.DYE_CRATES.values().forEach(crate -> {
            Block b = crate.get();
            if (!combined.contains(b)) combined.add(b);
        });

        cir.setReturnValue(combined.toArray(new Block[0]));
    }
}