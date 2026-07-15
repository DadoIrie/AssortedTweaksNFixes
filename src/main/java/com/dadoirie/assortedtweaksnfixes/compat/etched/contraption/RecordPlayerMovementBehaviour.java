package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public abstract class RecordPlayerMovementBehaviour implements MovementBehaviour {

    @Override
    public void stopMoving(MovementContext context) {
        if (context.world.isClientSide)
            return;

        // context.blockEntityData aliases the assembly-time nbt; interactions replace the blocks-map
        // entry with a fresh info+nbt, and the blocks map is what actually lands in the world
        StructureBlockInfo info = context.contraption.getBlocks().get(context.localPos);
        if (info == null || info.nbt() == null)
            return;
        if (writeSyncData(info, context.world.registryAccess()) == null)
            return;

        ContraptionLandingHandler.stamp(info.nbt(), context.contraption.entity);
    }

    // null when this block has nothing playable; payload keys are interpreted by ContraptionSoundManager
    @Nullable
    protected abstract CompoundTag writeSyncData(StructureBlockInfo info, HolderLookup.Provider registries);
}
