package com.dadoirie.assortedtweaksnfixes.mixin.etched.jukeboxblockentitytracker;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.compat.etched.ClientboundJukeboxLandedPacket;
import com.dadoirie.assortedtweaksnfixes.compat.etched.ContraptionPositionLogger;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.trains.entity.Carriage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContraptionEntity.class)
public abstract class ContraptionDisassemblyMixin {

    @Unique
    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(ContraptionDisassemblyMixin.class);

    @Shadow
    protected abstract StructureTransform makeStructureTransform();

    @Inject(method = "disassemble", at = @At("HEAD"))
    private void atf_captureLandingPositions(CallbackInfo ci) {
        AbstractContraptionEntity self = (AbstractContraptionEntity) (Object) this;
        if (!self.isAlive() || self.getContraption() == null)
            return;
        if (!(self.level() instanceof ServerLevel serverLevel))
            return;

        StructureTransform transform = this.makeStructureTransform();

        for (ContraptionPositionLogger.TrackedPosition tracked : ContraptionPositionLogger.ATNF$TRACKED) {
            if (!atf_matches(tracked, self))
                continue;

            BlockPos landingPos = transform.apply(tracked.localPos);
            GlobalPos global = GlobalPos.of(serverLevel.dimension(), landingPos);
            ContraptionPositionLogger.TRACKED_JUKEBOXES.add(global);
            LOGGER.info("Disassembling - returning jukebox to global tracking at {}", landingPos);

            PacketDistributor.sendToPlayersNear(serverLevel, null,
                    landingPos.getX(), landingPos.getY(), landingPos.getZ(), 64,
                    new ClientboundJukeboxLandedPacket(tracked.originalPos, landingPos));
        }
    }

    @Unique
    private static boolean atf_matches(ContraptionPositionLogger.TrackedPosition tracked, AbstractContraptionEntity self) {
        if (tracked.entity != null)
            return tracked.entity == self;

        if (tracked.carriage != null) {
            Carriage.DimensionalCarriageEntity dimensional = tracked.carriage.getDimensionalIfPresent(self.level().dimension());
            return dimensional != null && dimensional.entity.get() == self;
        }

        return false;
    }
}