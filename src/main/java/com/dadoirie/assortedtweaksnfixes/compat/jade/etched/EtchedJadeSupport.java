package com.dadoirie.assortedtweaksnfixes.compat.jade.etched;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
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

// only ever referenced from ATNFJadePlugin behind an "etched" isLoaded check, so RadioBlock/RadioBlockEntity
// are never classloaded unless etched is actually present
public final class EtchedJadeSupport {

    private EtchedJadeSupport() {
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(RadioUrlProvider.INSTANCE, RadioBlock.class);
    }

    private enum RadioUrlProvider implements IBlockComponentProvider {
        INSTANCE;

        private static final ResourceLocation UID = ATNFConstants.identifer("radio_url");

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!(accessor.getBlockEntity() instanceof RadioBlockEntity radio))
                return;

            String url = radio.getUrl();
            if (url != null && !url.isEmpty()) {
                tooltip.add(Component.literal(url).withStyle(ChatFormatting.GRAY));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }
}
