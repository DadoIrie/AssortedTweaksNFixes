package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.compat.etched.ClientboundJukeboxStopPacket;
import gg.moonflower.etched.api.record.PlayableRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityTrackerMixin {

    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(JukeboxBlockEntityTrackerMixin.class);

    @Inject(method = "setTheItem", at = @At("HEAD"))
    private void atnf_stopOnRecordRemoved(ItemStack stack, CallbackInfo ci) {
        JukeboxBlockEntity self = (JukeboxBlockEntity) (Object) this;

        if (!(self.getLevel() instanceof ServerLevel serverLevel))
            return;
        if (PlayableRecord.isPlayableRecord(stack))
            return;

        BlockPos pos = self.getBlockPos();
        PacketDistributor.sendToPlayersNear(serverLevel, null, pos.getX(), pos.getY(), pos.getZ(), 64,
                new ClientboundJukeboxStopPacket(pos));
    }
}