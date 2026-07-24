package com.dadoirie.assortedtweaksnfixes.compat.jade.create;

import com.dadoirie.assortedtweaksnfixes.compat.jade.ContraptionBlockRaycast;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.callback.JadeRayTraceCallback;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.FluidView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ItemViewUtils;
import snownee.jade.api.view.ViewGroup;
import snownee.jade.impl.ObjectDataCenter;
import snownee.jade.util.JadeForgeUtils;

import java.util.ArrayList;
import java.util.List;

public final class ContraptionHoveredStorageJadeSupport {

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("create", "atnf_contraption_hovered_storage");
    private static final ResourceLocation FLUID_UID = ResourceLocation.fromNamespaceAndPath("create", "atnf_contraption_hovered_fluid_storage");
    private static final ResourceLocation CROUCH_REVERT = ResourceLocation.fromNamespaceAndPath("create", "atnf_contraption_hovered_storage.crouch_revert");

    private ContraptionHoveredStorageJadeSupport() {
    }

    public static void register(IWailaCommonRegistration registration) {
        registration.registerItemStorage(HoveredStorageProvider.INSTANCE, AbstractContraptionEntity.class);
        registration.registerFluidStorage(HoveredFluidProvider.INSTANCE, AbstractContraptionEntity.class);
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(UID, true);
        registration.addConfig(CROUCH_REVERT, false);
        registration.registerItemStorageClient(HoveredStorageProvider.INSTANCE);
        registration.registerFluidStorageClient(HoveredFluidProvider.INSTANCE);
        registration.addRayTraceCallback(new ShowDetailsWatcher());
    }

    private static boolean yieldsToCombined(Accessor<?> accessor) {
        if (!IWailaConfig.get().getPlugin().get(UID))
            return true;
        return accessor.showDetails() == IWailaConfig.get().getPlugin().get(CROUCH_REVERT);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    private static String hoveredGroupId(Accessor<?> accessor) {
        if (!(accessor instanceof EntityAccessor entityAccessor)
                || !(entityAccessor.getEntity() instanceof AbstractContraptionEntity contraptionEntity))
            return null;

        return ContraptionBlockRaycast.findTargetedBlock(entityAccessor, contraptionEntity)
                .map(info -> String.valueOf(info.pos().asLong()))
                .orElse(null);
    }

    @OnlyIn(Dist.CLIENT)
    private static final class ShowDetailsWatcher implements JadeRayTraceCallback {

        private int lastContraptionId = -1;
        private boolean lastShowDetails;

        @Nullable
        @Override
        public Accessor<?> onRayTrace(HitResult hitResult, @Nullable Accessor<?> accessor, @Nullable Accessor<?> originalAccessor) {
            if (!(accessor instanceof EntityAccessor entityAccessor)
                    || !(entityAccessor.getEntity() instanceof AbstractContraptionEntity contraptionEntity)
                    || !IWailaConfig.get().getPlugin().get(UID)) {
                lastContraptionId = -1;
                return accessor;
            }

            boolean showDetails = accessor.showDetails();
            if (lastContraptionId == contraptionEntity.getId() && lastShowDetails != showDetails) {
                ObjectDataCenter.requestServerData();
            }
            lastContraptionId = contraptionEntity.getId();
            lastShowDetails = showDetails;
            return accessor;
        }
    }

    private enum HoveredStorageProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
        INSTANCE;

        @Nullable
        @Override
        public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
            if (yieldsToCombined(accessor))
                return null;

            if (!(accessor.getTarget() instanceof AbstractContraptionEntity contraptionEntity))
                return null;

            List<ViewGroup<ItemStack>> result = new ArrayList<>();
            contraptionEntity.getContraption().getStorage().getAllItemStorages().forEach((pos, storage) -> {
                List<ViewGroup<ItemStack>> groups = ItemViewUtils.groupOf(storage, accessor, acc -> storage);
                if (groups == null)
                    return;
                for (ViewGroup<ItemStack> group : groups) {
                    group.id = String.valueOf(pos.asLong());
                    result.add(group);
                }
            });
            return result;
        }

        @Override
        public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
            String hovered = hoveredGroupId(accessor);
            if (hovered == null)
                return List.of();

            return ClientViewGroup.map(groups.stream().filter(group -> hovered.equals(group.id)).toList(), ItemView::new, null);
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        @Override
        public int getDefaultPriority() {
            return TooltipPosition.HEAD;
        }
    }

    private enum HoveredFluidProvider implements IServerExtensionProvider<CompoundTag>, IClientExtensionProvider<CompoundTag, FluidView> {
        INSTANCE;

        @Nullable
        @Override
        public List<ViewGroup<CompoundTag>> getGroups(Accessor<?> accessor) {
            if (yieldsToCombined(accessor))
                return null;

            if (!(accessor.getTarget() instanceof AbstractContraptionEntity contraptionEntity))
                return null;

            List<ViewGroup<CompoundTag>> result = new ArrayList<>();
            contraptionEntity.getContraption().getStorage().getFluids().storages.forEach((pos, storage) -> {
                for (ViewGroup<CompoundTag> group : JadeForgeUtils.fromFluidHandler(storage)) {
                    group.id = String.valueOf(pos.asLong());
                    result.add(group);
                }
            });
            return result;
        }

        @Override
        public List<ClientViewGroup<FluidView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<CompoundTag>> groups) {
            String hovered = hoveredGroupId(accessor);
            if (hovered == null)
                return List.of();

            return ClientViewGroup.map(groups.stream().filter(group -> hovered.equals(group.id)).toList(), FluidView::readDefault, null);
        }

        @Override
        public ResourceLocation getUid() {
            return FLUID_UID;
        }

        @Override
        public int getDefaultPriority() {
            return TooltipPosition.HEAD;
        }
    }
}
