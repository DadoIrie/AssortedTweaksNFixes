package com.dadoirie.assortedtweaksnfixes.compat.create.contraption;

import com.coolerpromc.fletchingrecipe.screen.FletchingTableMenu;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CartographyTableBlock;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.FletchingTableBlock;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.LoomBlock;
import net.minecraft.world.level.block.SmithingTableBlock;
import net.minecraft.world.level.block.StonecutterBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.Predicate;

public class MenuBlockInteractionBehaviour extends ATNFInteractionBehaviour {

    @FunctionalInterface
    private interface MenuFactory {
        AbstractContainerMenu create(int containerId, Inventory inventory, ContainerLevelAccess access, Predicate<Player> valid);
    }

    private static final MenuBlockInteractionBehaviour CRAFTING_TABLE = new MenuBlockInteractionBehaviour(CraftingTableBlock.class, "container.crafting",
            (id, inventory, access, valid) -> new CraftingMenu(id, inventory, access) {
                @Override public boolean stillValid(@NonNull Player player) { return valid.test(player); }
            });

    private static final MenuBlockInteractionBehaviour SMITHING_TABLE = new MenuBlockInteractionBehaviour(SmithingTableBlock.class, "container.upgrade",
            (id, inventory, access, valid) -> new SmithingMenu(id, inventory, access) {
                @Override public boolean stillValid(@NonNull Player player) { return valid.test(player); }
            });

    private static final MenuBlockInteractionBehaviour STONECUTTER = new MenuBlockInteractionBehaviour(StonecutterBlock.class, "container.stonecutter",
            (id, inventory, access, valid) -> new StonecutterMenu(id, inventory, access) {
                @Override public boolean stillValid(@NonNull Player player) { return valid.test(player); }
            });

    private static final MenuBlockInteractionBehaviour LOOM = new MenuBlockInteractionBehaviour(LoomBlock.class, "container.loom",
            (id, inventory, access, valid) -> new LoomMenu(id, inventory, access) {
                @Override public boolean stillValid(@NonNull Player player) { return valid.test(player); }
            });

    private static final MenuBlockInteractionBehaviour CARTOGRAPHY_TABLE = new MenuBlockInteractionBehaviour(CartographyTableBlock.class, "container.cartography_table",
            (id, inventory, access, valid) -> new CartographyTableMenu(id, inventory, access) {
                @Override public boolean stillValid(@NonNull Player player) { return valid.test(player); }
            });

    private static final MenuBlockInteractionBehaviour GRINDSTONE = new MenuBlockInteractionBehaviour(GrindstoneBlock.class, "container.grindstone_title",
            (id, inventory, access, valid) -> new GrindstoneMenu(id, inventory, access) {
                @Override public boolean stillValid(@NonNull Player player) { return valid.test(player); }
            });

    // private static final MenuBlockInteractionBehaviour ANVIL = new MenuBlockInteractionBehaviour(AnvilBlock.class, "container.repair",
    //        (id, inventory, access, valid) -> new AnvilMenu(id, inventory, access) {
    //            @Override public boolean stillValid(@NonNull Player player) { return valid.test(player); }
    //        });

    private static final Map<Class<? extends Block>, MenuBlockInteractionBehaviour> BEHAVIOURS = Map.of(
            // AnvilBlock.class, ANVIL,
            CraftingTableBlock.class, CRAFTING_TABLE,
            SmithingTableBlock.class, SMITHING_TABLE,
            StonecutterBlock.class, STONECUTTER,
            LoomBlock.class, LOOM,
            CartographyTableBlock.class, CARTOGRAPHY_TABLE,
            GrindstoneBlock.class, GRINDSTONE);

    @Nullable
    public static MenuBlockInteractionBehaviour forBlock(Block block, boolean fletchingRecipeLoaded) {
        for (Class<?> type = block.getClass(); type != null; type = type.getSuperclass()) {
            if (type == FletchingTableBlock.class)
                return fletchingRecipeLoaded ? FletchingRecipeSupport.FLETCHING_TABLE : null;
            MenuBlockInteractionBehaviour behaviour = BEHAVIOURS.get(type);
            if (behaviour != null)
                return behaviour;
        }
        return null;
    }

    private final Class<? extends Block> blockClass;
    private final Component title;
    private final MenuFactory menuFactory;

    private MenuBlockInteractionBehaviour(Class<? extends Block> blockClass, String titleKey, MenuFactory menuFactory) {
        this.blockClass = blockClass;
        this.title = Component.translatable(titleKey);
        this.menuFactory = menuFactory;
    }

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
                                           AbstractContraptionEntity contraptionEntity) {
        if (player.level().isClientSide())
            return true;

        StructureBlockInfo info = contraptionEntity.getContraption().getBlocks().get(localPos);
        if (info == null || !this.blockClass.isInstance(info.state().getBlock()))
            return false;

        ContainerLevelAccess access = new ContraptionContainerLevelAccess(player.level(), contraptionEntity, localPos);
        player.openMenu(new SimpleMenuProvider(
                (id, inventory, menuPlayer) -> this.menuFactory.create(id, inventory, access,
                        p -> stillValid(contraptionEntity, localPos, this.blockClass, p)),
                this.title));
        return true;
    }

    private static class FletchingRecipeSupport {

        private static final MenuBlockInteractionBehaviour FLETCHING_TABLE = new MenuBlockInteractionBehaviour(FletchingTableBlock.class, "block.minecraft.fletching_table",
                (id, inventory, access, valid) -> new FletchingTableMenu(id, inventory, access) {
                    @Override public boolean stillValid(@NonNull Player player) { return valid.test(player); }
                });
    }
}
