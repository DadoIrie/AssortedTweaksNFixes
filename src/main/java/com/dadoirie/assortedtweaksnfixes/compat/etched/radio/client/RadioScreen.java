package com.dadoirie.assortedtweaksnfixes.compat.etched.radio.client;

import com.dadoirie.assortedtweaksnfixes.ATNFConstants;
import com.dadoirie.assortedtweaksnfixes.compat.etched.radio.RadioMenu;
import com.dadoirie.assortedtweaksnfixes.compat.etched.radio.SetRadioMenuDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class RadioScreen extends AbstractContainerScreen<RadioMenu> {

    private static final ResourceLocation TEXTURE = ATNFConstants.identifer("textures/gui/radio.png");

    private EditBox name;
    private EditBox url;

    public RadioScreen(RadioMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.imageHeight = 57;
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RadioScreen::onRegisterMenuScreens);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(RadioMenu.TYPE.get(), RadioScreen::new);
    }

    @Override
    protected void init() {
        super.init();
        String nameText = this.name != null ? this.name.getValue() : this.menu.getInitialName();
        String urlText = this.url != null ? this.url.getValue() : this.menu.getInitialUrl();
        this.name = new EditBox(this.font, this.leftPos + 10, this.topPos + 21, 154, 16, null, Component.translatable("container." + ATNFConstants.MOD_ID + ".radio.name"));
        this.name.setMaxLength(32768);
        this.name.setValue(nameText);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.url = new EditBox(this.font, this.leftPos + 10, this.topPos + 39, 154, 16, null, Component.translatable("container.etched.radio.url"));
        this.url.setMaxLength(32768);
        this.url.setValue(urlText);
        this.url.setTextColor(-1);
        this.url.setTextColorUneditable(-1);
        this.url.setBordered(false);
        this.setFocused(this.url);
        this.addRenderableWidget(this.name);
        this.addRenderableWidget(this.url);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
            PacketDistributor.sendToServer(new SetRadioMenuDataPacket(this.url.getValue(), this.name.getValue()));
            Minecraft.getInstance().player.closeContainer();
        }).bounds((this.width - this.imageWidth) / 2, (this.height - this.imageHeight) / 2 + this.imageHeight + 5, this.imageWidth, 20).build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blit(TEXTURE, this.leftPos + 8, this.topPos + 18, 0, 57, 160, 14);
        guiGraphics.blit(TEXTURE, this.leftPos + 8, this.topPos + 36, 0, 57, 160, 14);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        EditBox focused = this.name.isFocused() ? this.name : this.url;
        return focused.keyPressed(keyCode, scanCode, modifiers)
                || (focused.isFocused() && focused.isVisible() && keyCode != GLFW_KEY_ESCAPE)
                || super.keyPressed(keyCode, scanCode, modifiers);
    }
}
