package com.dadoirie.assortedtweaksnfixes.compat.jade.etched;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.compat.jade.ContraptionBlockRaycast;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.common.block.RadioBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.config.IPluginConfig;

// only ever referenced from ATNFJadePlugin behind "create" + "etched" isLoaded checks, so
// AbstractContraptionEntity/RadioBlock are never classloaded unless both are actually present
public final class EtchedContraptionJadeSupport {

    private EtchedContraptionJadeSupport() {
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(Provider.INSTANCE, AbstractContraptionEntity.class);
    }

    private enum Provider implements IEntityComponentProvider {
        INSTANCE;

        private static final ResourceLocation UID = ATNFConstants.identifer("radio_url_contraption");
        private static final String URL_KEY = "Url";

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (!(accessor.getEntity() instanceof AbstractContraptionEntity contraptionEntity))
                return;

            StructureBlockInfo info = ContraptionBlockRaycast.findTargetedBlock(accessor, contraptionEntity).orElse(null);
            if (info == null || info.nbt() == null || !(info.state().getBlock() instanceof RadioBlock))
                return;

            String url = info.nbt().getString(URL_KEY);
            if (!url.isEmpty()) {
                tooltip.add(Component.literal(url).withStyle(ChatFormatting.GRAY));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }
}
