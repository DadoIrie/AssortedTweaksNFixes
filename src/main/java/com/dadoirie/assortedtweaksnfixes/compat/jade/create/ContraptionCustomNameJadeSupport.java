package com.dadoirie.assortedtweaksnfixes.compat.jade.create;

import com.dadoirie.assortedtweaksnfixes.compat.jade.ContraptionBlockRaycast;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.JadeIds;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public final class ContraptionCustomNameJadeSupport {

    private ContraptionCustomNameJadeSupport() {
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(CustomNameProvider.INSTANCE, AbstractContraptionEntity.class);
    }

    private enum CustomNameProvider implements IEntityComponentProvider {
        INSTANCE;

        private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("create", "atnf_contraption_custom_name");
        private static final String CUSTOM_NAME_KEY = "CustomName";

        @OnlyIn(Dist.CLIENT)
        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (!(accessor.getEntity() instanceof AbstractContraptionEntity contraptionEntity))
                return;

            StructureBlockInfo info = ContraptionBlockRaycast.findTargetedBlock(accessor, contraptionEntity).orElse(null);
            if (info == null || info.nbt() == null || !info.nbt().contains(CUSTOM_NAME_KEY, Tag.TAG_STRING))
                return;

            Component customName = BlockEntity.parseCustomNameSafe(
                    info.nbt().getString(CUSTOM_NAME_KEY), accessor.getLevel().registryAccess());
            if (customName == null)
                return;

            tooltip.remove(JadeIds.CORE_OBJECT_NAME);
            tooltip.add(0, IThemeHelper.get().title(customName), JadeIds.CORE_OBJECT_NAME);
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        @Override
        public int getDefaultPriority() {
            return TooltipPosition.HEAD + 1;
        }
    }
}
