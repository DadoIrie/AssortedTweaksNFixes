package com.dadoirie.assortedtweaksnfixes.compat.etched.radio;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.compat.etched.RadioNameAccess;
import gg.moonflower.etched.common.blockentity.RadioBlockEntity;
import gg.moonflower.etched.core.registry.EtchedBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RadioMenu extends AbstractContainerMenu {

    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ATNFConstants.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<RadioMenu>> TYPE =
            MENUS.register("radio", () -> IMenuTypeExtension.create(RadioMenu::new));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }

    private final ContainerLevelAccess access;
    private final String initialUrl;
    private final String initialName;

    public RadioMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, ContainerLevelAccess.NULL, extraData.readUtf(), extraData.readUtf());
    }

    public RadioMenu(int containerId, ContainerLevelAccess access) {
        this(containerId, access, "", "");
    }

    private RadioMenu(int containerId, ContainerLevelAccess access, String initialUrl, String initialName) {
        super(TYPE.get(), containerId);
        this.access = access;
        this.initialUrl = initialUrl;
        this.initialName = initialName;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, EtchedBlocks.RADIO.get());
    }

    public void setUrl(String url) {
        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof RadioBlockEntity be) {
                be.setUrl(url);
            }
        });
    }

    public void setName(String name) {
        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof RadioBlockEntity be) {
                ((RadioNameAccess) be).atnf$setName(name);
            }
        });
    }

    public String getInitialUrl() {
        return this.initialUrl;
    }

    public String getInitialName() {
        return this.initialName;
    }
}
