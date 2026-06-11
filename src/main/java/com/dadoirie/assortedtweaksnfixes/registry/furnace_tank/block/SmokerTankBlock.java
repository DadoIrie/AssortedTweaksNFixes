package com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.block;

import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.FurnaceTankRegistry;
import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.entity.AbstractFurnaceTankBlockEntity;
import com.dadoirie.assortedtweaksnfixes.registry.furnace_tank.entity.SmokerTankBlockEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SmokerTankBlock extends SmokerBlock {

    public SmokerTankBlock(Properties properties) {
        super(properties);
    }

    public static final MapCodec<SmokerBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(propertiesCodec()).apply(instance, SmokerTankBlock::new)
    );

    @Override
    public @NotNull MapCodec<SmokerBlock> codec() {
        return CODEC;
    }

    @Override
    protected void openContainer(Level worldIn, @NotNull BlockPos pos, @NotNull Player player) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof SmokerTankBlockEntity) {
            player.openMenu((MenuProvider) blockEntity);
            player.awardStat(Stats.INTERACT_WITH_SMOKER);
        }
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SmokerTankBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, FurnaceTankRegistry.SMOKER_TANK_ENTITY.get(), AbstractFurnaceTankBlockEntity::tick);
    }
}