package com.dadoirie.assortedtweaksnfixes.compat.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import gg.moonflower.etched.api.record.PlayableRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JukeboxMovementBehaviour implements MovementBehaviour {

    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(JukeboxMovementBehaviour.class);
    private static final String RECORD_ITEM_TAG = JukeboxBlockEntity.SONG_ITEM_TAG_ID;
    private static final String ORIGINAL_POS_TAG = "OriginalPos";

    static final Map<BlockPos, BlockPos> PENDING_ORIGINAL_POS = new ConcurrentHashMap<>();

    @Override
    public void startMoving(MovementContext context) {
        if (context.world.isClientSide)
            return;

        CompoundTag blockEntityData = context.blockEntityData;
        if (blockEntityData == null) {
            return;
        }
        if (!blockEntityData.contains(RECORD_ITEM_TAG, Tag.TAG_COMPOUND)) {
            return;
        }

        ItemStack stack = ItemStack.parse(context.world.registryAccess(), blockEntityData.getCompound(RECORD_ITEM_TAG))
                .orElse(ItemStack.EMPTY);
        if (!PlayableRecord.isPlayableRecord(stack)) {
            return;
        }

        BlockPos originalPos = context.localPos.offset(context.contraption.anchor);
        context.data.putLong(ORIGINAL_POS_TAG, originalPos.asLong());
    }

    @Override
    public void tick(MovementContext context) {
        if (!context.world.isClientSide)
            return;
        if (context.position == null)
            return;

        BlockPos originalPos = trackedOriginalPos(context);
        if (originalPos == null)
            return;

        ContraptionSoundClientHandler.repositionTracked(originalPos, context.position);
    }

    @Override
    public void stopMoving(MovementContext context) {
        if (context.world.isClientSide)
            return;

        BlockPos originalPos = trackedOriginalPos(context);
        if (originalPos == null)
            return;

        PENDING_ORIGINAL_POS.put(context.localPos, originalPos);
    }

    private static BlockPos trackedOriginalPos(MovementContext context) {
        if (!context.data.contains(ORIGINAL_POS_TAG))
            return null;
        return BlockPos.of(context.data.getLong(ORIGINAL_POS_TAG));
    }
}