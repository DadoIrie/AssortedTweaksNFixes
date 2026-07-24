package com.dadoirie.assortedtweaksnfixes.compat.create.contraption;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.BiFunction;

public class ContraptionContainerLevelAccess implements ContainerLevelAccess {

    private final Level level;
    private final AbstractContraptionEntity contraptionEntity;
    private final BlockPos localPos;

    public ContraptionContainerLevelAccess(Level level, AbstractContraptionEntity contraptionEntity, BlockPos localPos) {
        this.level = level;
        this.contraptionEntity = contraptionEntity;
        this.localPos = localPos;
    }

    @Override
    public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> levelPosFunction) {
        BlockPos globalPos = BlockPos.containing(this.contraptionEntity.toGlobalVector(Vec3.atCenterOf(this.localPos), 1));
        return Optional.of(levelPosFunction.apply(this.level, globalPos));
    }
}
