package com.dadoirie.assortedtweaksnfixes.compat.jade.etched;

import com.dadoirie.assortedtweaksnfixes.compat.jade.ContraptionBlockRaycast;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.common.block.RadioBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.config.IPluginConfig;

public final class EtchedContraptionJadeSupport {

    private EtchedContraptionJadeSupport() {
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(Provider.INSTANCE, AbstractContraptionEntity.class);
    }

    private enum Provider implements IEntityComponentProvider {
        INSTANCE;

        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("etched", "atnf_radio_url_contraption");
        private static final String NAME_KEY = "Name";
        private static final String URL_KEY = "Url";

        @OnlyIn(Dist.CLIENT)
        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (!(accessor.getEntity() instanceof AbstractContraptionEntity contraptionEntity))
                return;

            StructureBlockInfo info = ContraptionBlockRaycast.findTargetedBlock(accessor, contraptionEntity).orElse(null);
            if (info == null || info.nbt() == null || !(info.state().getBlock() instanceof RadioBlock))
                return;

            String displayValue = null;
            if (info.nbt().contains(NAME_KEY, Tag.TAG_STRING)) {
                String name = info.nbt().getString(NAME_KEY);
                if (!name.isEmpty()) {
                    displayValue = name;
                }
            }

            if (displayValue == null && info.nbt().contains(URL_KEY, Tag.TAG_STRING)) {
                String url = info.nbt().getString(URL_KEY);
                if (!url.isEmpty()) {
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