package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import com.dadoirie.assortedtweaksnfixes.compat.etched.RadioNameAccess;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(RadioBlockEntity.class)
public abstract class ExtendedRadioBlockEntityMixin extends BlockEntity implements RadioNameAccess {

    @Unique
    private String atnf$name;

    private ExtendedRadioBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void atnf$loadName(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        this.atnf$name = tag.contains("Name", Tag.TAG_STRING) ? tag.getString("Name") : null;
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void atnf$saveName(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.atnf$name != null) {
            tag.putString("Name", this.atnf$name);
        }
    }

    @Override
    @Unique
    public String atnf$getName() {
        return this.atnf$name;
    }

    @Override
    @Unique
    public void atnf$setName(String name) {
        if (Objects.equals(this.atnf$name, name)) {
            return;
        }
        this.atnf$name = name;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
