package com.dadoirie.assortedtweaksnfixes.compat.etched;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.function.Supplier;

@EventBusSubscriber(modid = AssortedTweaksNFixesConstants.MOD_ID)
public class ContraptionSoundNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                ClientboundJukeboxPositionPacket.TYPE,
                ClientboundJukeboxPositionPacket.CODEC,
                clientHandler(() -> ContraptionSoundClientHandler::handlePosition)
        );
        registrar.playToClient(
                ClientboundJukeboxLandedPacket.TYPE,
                ClientboundJukeboxLandedPacket.CODEC,
                clientHandler(() -> ContraptionSoundClientHandler::handleLanded)
        );
        registrar.playToClient(
                ClientboundJukeboxStopPacket.TYPE,
                ClientboundJukeboxStopPacket.CODEC,
                clientHandler(() -> ContraptionSoundClientHandler::handleStop)
        );
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> clientHandler(Supplier<IPayloadHandler<T>> handler) {
        if (FMLLoader.getDist() == Dist.CLIENT && ModList.get().isLoaded("etched")) {
            return handler.get();
        }
        return (payload, context) -> {};
    }
}