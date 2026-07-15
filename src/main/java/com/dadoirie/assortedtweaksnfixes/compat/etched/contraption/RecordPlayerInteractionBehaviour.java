package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class RecordPlayerInteractionBehaviour extends MovingInteractionBehaviour {

    static <T extends BlockEntity> void withBlockEntity(AbstractContraptionEntity contraptionEntity, BlockPos localPos,
                                                        BiFunction<BlockPos, BlockState, T> factory, Consumer<T> action) {
        AtomicReference<BlockState> state = new AtomicReference<>();
        T blockEntity = createBlockEntity(contraptionEntity, localPos, factory, state);
        if (blockEntity == null)
            return;

        action.accept(blockEntity);
        writeBack(contraptionEntity, localPos, state.get(), blockEntity);
    }

    @Nullable
    static <T extends BlockEntity> T createBlockEntity(AbstractContraptionEntity contraptionEntity, BlockPos localPos,
                                                       BiFunction<BlockPos, BlockState, T> factory, AtomicReference<BlockState> state) {
        StructureBlockInfo info = contraptionEntity.getContraption().getBlocks().get(localPos);
        if (info == null || info.nbt() == null)
            return null;

        Level realLevel = contraptionEntity.level();
        BlockPos realPos = BlockPos.containing(contraptionEntity.toGlobalVector(Vec3.atCenterOf(localPos), 1));
        state.set(info.state());

        T blockEntity = factory.apply(realPos, info.state());
        blockEntity.loadWithComponents(info.nbt(), realLevel.registryAccess());
        blockEntity.setLevel(new WrappedLevel(realLevel) {
            @Override
            public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
                if (realPos.equals(pos)) {
                    state.set(newState);
                    return true;
                }
                return false;
            }

            @Override
            public BlockState getBlockState(@Nullable BlockPos pos) {
                return realPos.equals(pos) ? state.get() : super.getBlockState(pos);
            }

            @Override
            public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
            }
        });
        return blockEntity;
    }

    static void writeBack(AbstractContraptionEntity contraptionEntity, BlockPos localPos, BlockState state, BlockEntity blockEntity) {
        contraptionEntity.setBlock(localPos,
                new StructureBlockInfo(localPos, state, blockEntity.saveWithoutMetadata(contraptionEntity.level().registryAccess())));
    }
}
