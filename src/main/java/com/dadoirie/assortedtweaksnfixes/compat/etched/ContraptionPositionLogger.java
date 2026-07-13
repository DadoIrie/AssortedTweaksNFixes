package com.dadoirie.assortedtweaksnfixes.compat.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@EventBusSubscriber
public class ContraptionPositionLogger {

    private static final Logger LOGGER = AssortedTweaksNFixesConstants.getLogger(ContraptionPositionLogger.class);

    public static final Set<GlobalPos> TRACKED_JUKEBOXES = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static final List<TrackedPosition> ATNF$TRACKED = new CopyOnWriteArrayList<>();
    public static final Map<Contraption, Map<BlockPos, BlockPos>> ATNF$PENDING_LOCAL_POSITIONS = new ConcurrentHashMap<>();

    private static int atnf$tickCounter = 0;

    public static void registerTrain(CarriageContraptionEntity entity, Carriage carriage) {
        Contraption contraption = entity.getContraption();
        if (contraption == null)
            return;

        Map<BlockPos, BlockPos> pendingLocalPositions = ATNF$PENDING_LOCAL_POSITIONS.remove(contraption);
        if (pendingLocalPositions == null)
            return;

        for (Map.Entry<BlockPos, BlockPos> pending : pendingLocalPositions.entrySet()) {
            ATNF$TRACKED.add(new TrackedPosition(carriage, pending.getKey(), pending.getValue()));
            LOGGER.info("ADDED carriage contraption tracking - localPos: {}", pending.getKey());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getState().getBlock() instanceof JukeboxBlock))
            return;
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;

        BlockPos pos = event.getPos();
        TRACKED_JUKEBOXES.remove(GlobalPos.of(serverLevel.dimension(), pos));

        PacketDistributor.sendToPlayersNear(serverLevel, null, pos.getX(), pos.getY(), pos.getZ(), 64,
                new ClientboundJukeboxStopPacket(pos));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (ATNF$TRACKED.isEmpty())
            return;

        atnf$tickCounter++;

        if (atnf$tickCounter % 20 == 0) {
            LOGGER.info("Heartbeat - ATNF$TRACKED: {}, TRACKED_JUKEBOXES: {}",
                    ATNF$TRACKED.size(), TRACKED_JUKEBOXES.size());
        }

        for (int i = ATNF$TRACKED.size() - 1; i >= 0; i--) {
            TrackedPosition tracked = ATNF$TRACKED.get(i);

            AbstractContraptionEntity entity = tracked.entity;

            if (tracked.carriage != null) {
                Carriage.DimensionalCarriageEntity dimensional =
                        tracked.carriage.getDimensional(event.getServer().overworld());

                entity = dimensional.entity.get();
            }

            if (entity == null) {
                continue;
            }

            if (atnf$tickCounter % 20 == 0) {
                LOGGER.info("Resolved entity: {} removed={} tickCount={}",
                        entity.getClass().getSimpleName(),
                        entity.isRemoved(),
                        entity.tickCount);
            }

            if (entity.isRemoved()) {
                ATNF$TRACKED.remove(i);
                continue;
            }

            Vec3 globalPos = entity.toGlobalVector(
                    Vec3.atLowerCornerOf(tracked.localPos),
                    1.0F
            );

            if (!globalPos.equals(tracked.lastPosition)) {
                tracked.lastPosition = globalPos;
                LOGGER.info("Jukebox moved to: {}", globalPos);

                if (entity.level() instanceof ServerLevel soundLevel) {
                    PacketDistributor.sendToPlayersNear(soundLevel, null, globalPos.x, globalPos.y, globalPos.z, 64,
                            new ClientboundJukeboxPositionPacket(tracked.originalPos, globalPos));
                }
            }
        }
    }

    public static class TrackedPosition {
        public final AbstractContraptionEntity entity;
        public final Carriage carriage;
        public final BlockPos localPos;
        public final BlockPos originalPos;
        Vec3 lastPosition;

        public TrackedPosition(AbstractContraptionEntity entity, BlockPos localPos, BlockPos originalPos) {
            LOGGER.info("TrackedPosition ENTITY constructor called: {}", entity);
            this.entity = entity;
            this.carriage = null;
            this.localPos = localPos;
            this.originalPos = originalPos;
        }

        public TrackedPosition(Carriage carriage, BlockPos localPos, BlockPos originalPos) {
            LOGGER.info("TrackedPosition CARRIAGE constructor called: {}", carriage);
            this.entity = null;
            this.carriage = carriage;
            this.localPos = localPos;
            this.originalPos = originalPos;
        }
    }
}