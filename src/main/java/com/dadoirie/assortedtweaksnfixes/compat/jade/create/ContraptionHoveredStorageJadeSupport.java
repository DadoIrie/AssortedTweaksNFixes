package com.dadoirie.assortedtweaksnfixes.compat.jade.create;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.compat.jade.ContraptionBlockRaycast;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ItemViewUtils;
import snownee.jade.api.view.ViewGroup;

import java.util.List;
import java.util.Optional;

// Jade Addons' own ContraptionItemStorageProvider always shows the merged contents of every item storage on
// the contraption at once, with no way to narrow that down. This registers a sibling provider for the same
// slot (same target class, higher priority) that, while not crouching, narrows the display down to only the
// storage actually under the crosshair; crouching (Jade's "show details" key) still shows everything, exactly
// like today. Returning null here falls through to Jade Addons' own provider unchanged, so this never touches
// Create or other Create addons - it only ever competes for which data Jade's existing item storage tooltip
// line renders.
public final class ContraptionHoveredStorageJadeSupport {

    private static final ResourceLocation UID = ATNFConstants.identifer("contraption_hovered_storage");

    private ContraptionHoveredStorageJadeSupport() {
    }

    public static void register(IWailaCommonRegistration registration) {
        registration.registerItemStorage(HoveredStorageProvider.INSTANCE, AbstractContraptionEntity.class);
    }

    public static void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(UID, true);
        registration.registerItemStorageClient(HoveredStorageProvider.INSTANCE);
    }

    private enum HoveredStorageProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
        INSTANCE;

        @Nullable
        @Override
        public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
            if (accessor.showDetails() || !IWailaConfig.get().getPlugin().get(UID))
                return null;

            if (!(accessor.getTarget() instanceof AbstractContraptionEntity contraptionEntity))
                return null;

            Optional<BlockPos> hovered = ContraptionBlockRaycast.findTargetedBlockPos(accessor.getPlayer(), contraptionEntity);
            if (hovered.isEmpty())
                return List.of();

            MountedItemStorage storage = contraptionEntity.getContraption().getStorage().getAllItemStorages().get(hovered.get());
            if (storage == null)
                return List.of();

            List<ViewGroup<ItemStack>> groups = ItemViewUtils.groupOf(storage, accessor, acc -> storage);
            return groups != null ? groups : List.of();
        }

        // the server-computed ViewGroup<ItemStack> data only reaches the client's tooltip renderer if a client
        // extension provider is registered under the same UID (see ContraptionItemStorageProvider, which
        // registers this exact same pairing) - without this, the client silently has nothing to map the
        // server's response to and renders no items at all
        @Override
        public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
            return ClientViewGroup.map(groups, ItemView::new, null);
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        @Override
        public int getDefaultPriority() {
            // must sort before Jade Addons' own ContraptionItemStorageProvider (default TooltipPosition.BODY)
            // so a non-null result here wins the "first provider to return data" race
            return TooltipPosition.HEAD;
        }
    }
}
