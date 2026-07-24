package com.dadoirie.assortedtweaksnfixes.compat.etched;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.compat.create.contraption.ContraptionBlockNbtS2C;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.AlbumJukeboxInteractionBehaviour;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.AlbumJukeboxMovementBehaviour;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.ContraptionLandingHandler;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.ContraptionRecordHandoffS2C;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.ContraptionRecordStopS2C;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.ContraptionRecordsData;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.JukeboxInteractionBehaviour;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.JukeboxMovementBehaviour;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.RadioInteractionBehaviour;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.RadioMovementBehaviour;
import com.dadoirie.assortedtweaksnfixes.compat.etched.contraption.client.ContraptionSoundManager;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.transformable.MovedBlockTransformerRegistries;
import dev.corgitaco.dataanchor.network.Packet;
import dev.corgitaco.dataanchor.network.S2CNetworkContainer;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

public class EtchedContraptionCompat {

    private static final S2CNetworkContainer NETWORK = S2CNetworkContainer.of(ATNFConstants.MOD_ID);

    public static void register(IEventBus modEventBus) {
        ContraptionRecordsData.init();
        registerPackets();
        modEventBus.addListener(EtchedContraptionCompat::onCommonSetup);

        if (FMLLoader.getDist() == Dist.CLIENT) {
            registerClientListeners();
        }
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MovementBehaviour.REGISTRY.register(Blocks.JUKEBOX, new JukeboxMovementBehaviour());
            MovementBehaviour.REGISTRY.register(EtchedBlocks.RADIO.get(), new RadioMovementBehaviour());
            MovementBehaviour.REGISTRY.register(EtchedBlocks.ALBUM_JUKEBOX.get(), new AlbumJukeboxMovementBehaviour());

            MovingInteractionBehaviour.REGISTRY.register(Blocks.JUKEBOX, new JukeboxInteractionBehaviour());
            MovingInteractionBehaviour.REGISTRY.register(EtchedBlocks.RADIO.get(), new RadioInteractionBehaviour());
            MovingInteractionBehaviour.REGISTRY.register(EtchedBlocks.ALBUM_JUKEBOX.get(), new AlbumJukeboxInteractionBehaviour());

            MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.register(BlockEntityType.JUKEBOX, ContraptionLandingHandler::onLanded);
            MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.register(EtchedBlocks.RADIO_BE.get(), ContraptionLandingHandler::onLanded);
            MovedBlockTransformerRegistries.BLOCK_ENTITY_TRANSFORMERS.register(EtchedBlocks.ALBUM_JUKEBOX_BE.get(), ContraptionLandingHandler::onLanded);
        });
    }

    private static void registerPackets() {
        NETWORK.registerPacketHandler(new Packet.Handler<>(
                ContraptionRecordHandoffS2C.class,
                ContraptionRecordHandoffS2C.TYPE,
                ContraptionRecordHandoffS2C.STREAM_CODEC,
                ContraptionRecordHandoffS2C::handle));
        NETWORK.registerPacketHandler(new Packet.Handler<>(
                ContraptionRecordStopS2C.class,
                ContraptionRecordStopS2C.TYPE,
                ContraptionRecordStopS2C.STREAM_CODEC,
                ContraptionRecordStopS2C::handle));
        NETWORK.registerPacketHandler(new Packet.Handler<>(
                ContraptionBlockNbtS2C.class,
                ContraptionBlockNbtS2C.TYPE,
                ContraptionBlockNbtS2C.STREAM_CODEC,
                ContraptionBlockNbtS2C::handle));
    }

    private static void registerClientListeners() {
        NeoForge.EVENT_BUS.addListener(ContraptionSoundManager::onClientTick);
    }
}
