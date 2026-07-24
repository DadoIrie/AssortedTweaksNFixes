package com.dadoirie.assortedtweaksnfixes.compat.etched.radio;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetRadioMenuDataPacket(String url, String name) implements CustomPacketPayload {

    public static final Type<SetRadioMenuDataPacket> TYPE = new Type<>(ATNFConstants.identifer("set_radio_menu_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetRadioMenuDataPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SetRadioMenuDataPacket::url,
            ByteBufCodecs.STRING_UTF8, SetRadioMenuDataPacket::name,
            SetRadioMenuDataPacket::new);

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(SetRadioMenuDataPacket::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(TYPE, CODEC, SetRadioMenuDataPacket::handle);
    }

    private static void handle(SetRadioMenuDataPacket packet, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof RadioMenu menu) {
            menu.setUrl(packet.url());
            menu.setName(packet.name());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
