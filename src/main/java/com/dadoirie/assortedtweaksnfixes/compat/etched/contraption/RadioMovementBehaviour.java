package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.common.block.RadioBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public class RadioMovementBehaviour extends EtchedMovementBehaviour {

    @Override
    @Nullable
    public CompoundTag writeSyncData(StructureBlockInfo info, HolderLookup.Provider registries) {
        BlockState state = info.state();
        if (state.hasProperty(RadioBlock.POWERED) && state.getValue(RadioBlock.POWERED))
            return null;

        String url = info.nbt().getString("Url");
        if (!TrackData.isValidURL(url))
            return null;

        CompoundTag payload = new CompoundTag();
        payload.putString("url", url);
        return payload;
    }
}
