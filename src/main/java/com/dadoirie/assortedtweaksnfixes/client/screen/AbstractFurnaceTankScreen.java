package com.dadoirie.assortedtweaksnfixes.client.screen;

import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.menu.AbstractFurnaceTankMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractFurnaceTankScreen<T extends AbstractFurnaceTankMenu> extends AbstractFurnaceScreen<T> {
    private ImageButton recipeButton;

    public AbstractFurnaceTankScreen(T menu, AbstractFurnaceRecipeBookComponent recipeBookComponent, Inventory playerInventory, Component title, ResourceLocation texture, ResourceLocation litProgressSprite, ResourceLocation burnProgressSprite) {
        super(menu, recipeBookComponent, playerInventory, title, texture, litProgressSprite, burnProgressSprite);
    }

    @Override
    public void init() {
        super.init();
        this.recipeButton = this.children().stream()
                .filter(ImageButton.class::isInstance)
                .map(ImageButton.class::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.recipeButton != null) {
            this.recipeButton.setPosition(this.leftPos + 145, this.topPos + 15);
        }
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        int x = this.leftPos;
        int y = this.topPos;
    }
}