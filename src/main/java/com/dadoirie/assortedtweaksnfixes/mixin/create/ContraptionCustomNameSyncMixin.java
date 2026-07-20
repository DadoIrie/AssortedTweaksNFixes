package com.dadoirie.assortedtweaksnfixes.mixin.create;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

// Create intentionally sends an empty updateTag to the client for storage blocks (chest, barrel, etc.) on
// contraption spawn, since their contents are synced separately through MountedStorageManager - that's left
// completely untouched here, on purpose, since duplicating item contents into the block-list sync is exactly
// what Create is avoiding. Only the block's custom name (if any) is added onto that already-blank updateTag,
// so it rides the same fast, existing contraption spawn sync radio-type blocks already get for free, instead
// of needing a slow on-demand round trip just to show it in a tooltip.
@Mixin(Contraption.class)
public abstract class ContraptionCustomNameSyncMixin {

    @Shadow
    public BlockPos anchor;

    @Shadow
    protected Map<BlockPos, CompoundTag> updateTags;

    @Inject(method = "addBlock", at = @At("TAIL"))
    private void atnf$syncCustomName(Level level, BlockPos pos, Pair<StructureBlockInfo, BlockEntity> pair, CallbackInfo ci) {
        BlockEntity be = pair.getValue();
        if (!(be instanceof Nameable nameable) || !nameable.hasCustomName())
            return;

        BlockPos localPos = pos.subtract(this.anchor);
        CompoundTag updateTag = this.updateTags.computeIfAbsent(localPos, unused -> new CompoundTag());
        updateTag.putString("CustomName", Component.Serializer.toJson(nameable.getCustomName(), level.registryAccess()));
    }
}
