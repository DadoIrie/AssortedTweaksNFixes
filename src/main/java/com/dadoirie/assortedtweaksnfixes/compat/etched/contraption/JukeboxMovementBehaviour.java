package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import gg.moonflower.etched.api.record.PlayableRecord;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;

public class JukeboxMovementBehaviour extends EtchedMovementBehaviour {

    public static ItemStack readRecord(CompoundTag blockEntityNbt, HolderLookup.Provider registries) {
        if (blockEntityNbt == null || !blockEntityNbt.contains(JukeboxBlockEntity.SONG_ITEM_TAG_ID, Tag.TAG_COMPOUND))
            return ItemStack.EMPTY;
        return ItemStack.parse(registries, blockEntityNbt.getCompound(JukeboxBlockEntity.SONG_ITEM_TAG_ID))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    @Nullable
    public CompoundTag writeSyncData(StructureBlockInfo info, HolderLookup.Provider registries) {
        if (!PlayableRecord.isPlayableRecord(readRecord(info.nbt(), registries)))
            return null;

        CompoundTag payload = new CompoundTag();
        payload.put("record", info.nbt().getCompound(JukeboxBlockEntity.SONG_ITEM_TAG_ID).copy());
        return payload;
    }
}
