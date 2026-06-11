package com.dadoirie.assortedtweaksnfixes.registry.furnace_tank;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.block.BlastFurnaceTankBlock;
import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.block.FurnaceTankBlock;
import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.block.SmokerTankBlock;
import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.entity.BlastFurnaceTankBlockEntity;
import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.entity.FurnaceTankBlockEntity;
import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.entity.SmokerTankBlockEntity;
import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FurnaceTankRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, AssortedTweaksNFixesConstants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AssortedTweaksNFixesConstants.MOD_ID);

    public static final DeferredHolder<Block, Block> FURNACE_TANK = BLOCKS.register("furnace_tank", () -> new FurnaceTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE)));
    public static final DeferredHolder<Block, Block> BLAST_FURNACE_TANK = BLOCKS.register("blast_furnace_tank", () -> new BlastFurnaceTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLAST_FURNACE)));
    public static final DeferredHolder<Block, Block> SMOKER_TANK = BLOCKS.register("smoker_tank", () -> new SmokerTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMOKER)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FurnaceTankBlockEntity>> FURNACE_TANK_ENTITY = BLOCK_ENTITIES.register("furnace_tank",
            () -> BlockEntityType.Builder.of(FurnaceTankBlockEntity::new, FURNACE_TANK.get())
                    .build(DSL.remainderType()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlastFurnaceTankBlockEntity>> BLAST_FURNACE_TANK_ENTITY = BLOCK_ENTITIES.register("blast_furnace_tank",
            () -> BlockEntityType.Builder.of(BlastFurnaceTankBlockEntity::new, BLAST_FURNACE_TANK.get())
                    .build(DSL.remainderType()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmokerTankBlockEntity>> SMOKER_TANK_ENTITY = BLOCK_ENTITIES.register("smoker_tank",
            () -> BlockEntityType.Builder.of(SmokerTankBlockEntity::new, SMOKER_TANK.get())
                    .build(DSL.remainderType()));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }
}