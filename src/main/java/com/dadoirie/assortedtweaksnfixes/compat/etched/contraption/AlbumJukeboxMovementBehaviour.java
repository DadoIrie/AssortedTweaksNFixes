package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import gg.moonflower.etched.common.block.AlbumJukeboxBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public class AlbumJukeboxMovementBehaviour extends EtchedMovementBehaviour {

    @Override
    @Nullable
    public CompoundTag writeSyncData(StructureBlockInfo info, HolderLookup.Provider registries) {
        BlockState state = info.state();
        if (state.hasProperty(AlbumJukeboxBlock.POWERED) && state.getValue(AlbumJukeboxBlock.POWERED))
            return null;

        if (info.nbt() == null || !info.nbt().contains("Items", Tag.TAG_LIST))
            return null;

        ListTag items = info.nbt().getList("Items", Tag.TAG_COMPOUND);
        if (items.isEmpty())
            return null;

        CompoundTag payload = new CompoundTag();
        payload.put("items", items.copy());
        return payload;
    }
}
