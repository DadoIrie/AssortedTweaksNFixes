package com.dadoirie.assortedtweaksnfixes.compat.etched.contraption;

import com.dadoirie.assortedtweaksnfixes.compat.create.contraption.ATNFInteractionBehaviour;
import com.dadoirie.assortedtweaksnfixes.compat.etched.RadioNameAccess;
import com.dadoirie.assortedtweaksnfixes.compat.etched.radio.RadioMenu;
import com.dadoirie.assortedtweaksnfixes.mixin.ConditionalMixinPlugin;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import gg.moonflower.etched.common.block.RadioBlock;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import gg.moonflower.etched.core.Etched;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class RadioInteractionBehaviour extends ATNFInteractionBehaviour {

    private static final Component CONTAINER_TITLE = Component.translatable("container." + Etched.MOD_ID + ".radio");

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
                                           AbstractContraptionEntity contraptionEntity) {
        if (player.level().isClientSide()) {
            return true;
        }

        StructureBlockInfo info = contraptionEntity.getContraption().getBlocks().get(localPos);
        if (info == null || !(info.state().getBlock() instanceof RadioBlock)) {
            return false;
        }

        if (ConditionalMixinPlugin.isApplied("etched.ExtendedRadioBlockEntityMixin")) {
            openExtendedMenu(player, contraptionEntity, localPos, info);
        } else {
            openVanillaMenu(player, contraptionEntity, localPos, info);
        }

        return true;
    }

    private void openVanillaMenu(Player player, AbstractContraptionEntity contraptionEntity, BlockPos localPos, StructureBlockInfo info) {
        String url = info.nbt() != null ? info.nbt().getString("Url") : "";
        MenuProvider provider = new SimpleMenuProvider(
                (int id, Inventory inventory, Player menuPlayer) -> createStandardMenu(id, contraptionEntity, localPos),
                CONTAINER_TITLE
        );
        player.openMenu(provider, (RegistryFriendlyByteBuf buf) -> buf.writeUtf(url));
    }

    private void openExtendedMenu(Player player, AbstractContraptionEntity contraptionEntity, BlockPos localPos, StructureBlockInfo info) {
        String url = info.nbt() != null ? info.nbt().getString("Url") : "";
        String name = info.nbt() != null ? info.nbt().getString("Name") : "";

        MenuProvider provider = new SimpleMenuProvider(
                (int id, Inventory inventory, Player menuPlayer) -> createExtendedMenu(id, contraptionEntity, localPos),
                CONTAINER_TITLE
        );

        player.openMenu(provider, (RegistryFriendlyByteBuf buf) -> {
            buf.writeUtf(url);
            buf.writeUtf(name);
        });
    }

    private gg.moonflower.etched.common.menu.RadioMenu createStandardMenu(int containerId, AbstractContraptionEntity contraptionEntity, BlockPos localPos) {
        return new gg.moonflower.etched.common.menu.RadioMenu(containerId, ContainerLevelAccess.NULL) {
            @Override
            public boolean stillValid(Player player) {
                return ATNFInteractionBehaviour.stillValid(contraptionEntity, localPos, RadioBlock.class, player);
            }

            @Override
            public void setUrl(String url) {
                withBlockEntity(contraptionEntity, localPos, RadioBlockEntity::new, be -> be.setUrl(url));
                ContraptionRecordsData.sync(contraptionEntity);
            }
        };
    }

    private RadioMenu createExtendedMenu(int containerId, AbstractContraptionEntity contraptionEntity, BlockPos localPos) {
        return new RadioMenu(containerId, ContainerLevelAccess.NULL) {
            @Override
            public boolean stillValid(Player player) {
                return ATNFInteractionBehaviour.stillValid(contraptionEntity, localPos, RadioBlock.class, player);
            }

            @Override
            public void setUrl(String url) {
                withBlockEntity(contraptionEntity, localPos, RadioBlockEntity::new, be -> be.setUrl(url));
                ContraptionRecordsData.sync(contraptionEntity);
            }

            @Override
            public void setName(String name) {
                withBlockEntity(contraptionEntity, localPos, RadioBlockEntity::new, be -> ((RadioNameAccess) be).atnf$setName(name));
            }
        };
    }
}