    package com.dadoirie.assortedtweaksnfixes.content.furnace_tank.block;

    import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.FurnaceTankRegistry;
    import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.entity.AbstractFurnaceTankBlockEntity;
    import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.entity.FurnaceTankBlockEntity;
    import com.mojang.serialization.MapCodec;
    import com.mojang.serialization.codecs.RecordCodecBuilder;
    import net.minecraft.core.BlockPos;
    import net.minecraft.stats.Stats;
    import net.minecraft.world.MenuProvider;
    import net.minecraft.world.entity.player.Player;
    import net.minecraft.world.level.Level;
    import net.minecraft.world.level.block.AbstractFurnaceBlock;
    import net.minecraft.world.level.block.entity.BlockEntity;
    import net.minecraft.world.level.block.entity.BlockEntityTicker;
    import net.minecraft.world.level.block.entity.BlockEntityType;
    import net.minecraft.world.level.block.state.BlockState;
    import org.jetbrains.annotations.NotNull;
    import org.jetbrains.annotations.Nullable;

    public class FurnaceTankBlock extends AbstractFurnaceBlock {

        public FurnaceTankBlock(Properties properties) {
            super(properties);
        }
        public static final MapCodec<FurnaceTankBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(propertiesCodec()).apply(instance, FurnaceTankBlock::new)
        );
        @Override
        protected @NotNull MapCodec<? extends AbstractFurnaceBlock> codec() {
            return CODEC;
        }

        @Override
        protected void openContainer(Level worldIn, @NotNull BlockPos pos, @NotNull Player player) {
            BlockEntity blockEntity = worldIn.getBlockEntity(pos);
            if (blockEntity instanceof FurnaceTankBlockEntity) {
                player.openMenu((MenuProvider)blockEntity);
                player.awardStat(Stats.INTERACT_WITH_FURNACE);
            }

        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
            return new FurnaceTankBlockEntity(pos, state);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
            return createTickerHelper(type, FurnaceTankRegistry.FURNACE_TANK_ENTITY.get(), AbstractFurnaceTankBlockEntity::tick);
        }
    }