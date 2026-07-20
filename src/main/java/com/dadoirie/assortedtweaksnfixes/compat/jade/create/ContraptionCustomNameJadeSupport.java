package com.dadoirie.assortedtweaksnfixes.compat.jade.create;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.compat.jade.ContraptionBlockRaycast;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.JadeIds;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

// generic Create contraption support, unrelated to any specific block-adding mod
public final class ContraptionCustomNameJadeSupport {

    private ContraptionCustomNameJadeSupport() {
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(CustomNameProvider.INSTANCE, AbstractContraptionEntity.class);
    }

    private enum CustomNameProvider implements IEntityComponentProvider {
        INSTANCE;

        private static final ResourceLocation UID = ATNFConstants.identifer("contraption_custom_name");
        private static final String CUSTOM_NAME_KEY = "CustomName";

        // ContraptionCustomNameSyncMixin makes sure a named storage block's custom name rides the
        // contraption's own client sync, so it's already present in the client's local structure data by
        // the time this runs - no server round trip needed, same as the radio url tooltip
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
            // Jade Addons' own ContraptionExactBlockProvider also replaces the title at TooltipPosition.HEAD
            // (with the generic block name, since its synthetic BlockAccessor has no block entity to read a
            // custom name from) - run one step after it so our replacement is the one that actually sticks
            return TooltipPosition.HEAD + 1;
        }
    }
}
