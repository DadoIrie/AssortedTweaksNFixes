package com.dadoirie.assortedtweaksnfixes.compat.jade.etched;

import com.dadoirie.assortedtweaksnfixes.compat.etched.RadioNameAccess;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.config.IPluginConfig;

public final class EtchedJadeSupport {

    private EtchedJadeSupport() {
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(RadioUrlProvider.INSTANCE, RadioBlock.class);
    }

    private enum RadioUrlProvider implements IBlockComponentProvider {
        INSTANCE;

        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("etched", "atnf_radio_url");

        @OnlyIn(Dist.CLIENT)
        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!(accessor.getBlockEntity() instanceof RadioBlockEntity radio))
                return;

            String displayValue = null;
            if (radio instanceof RadioNameAccess nameAccess) {
                String name = nameAccess.atnf$getName();
                if (name != null && !name.isEmpty()) {
                    displayValue = name;
                }
            }

            if (displayValue == null) {
                String url = radio.getUrl();
                if (url != null && !url.isEmpty()) {
                    displayValue = url;
                }
            }

            if (displayValue != null) {
                tooltip.add(Component.literal(displayValue).withStyle(ChatFormatting.DARK_GREEN));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }
}