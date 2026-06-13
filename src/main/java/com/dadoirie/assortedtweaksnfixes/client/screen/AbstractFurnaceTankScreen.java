package com.dadoirie.assortedtweaksnfixes.client.screen;

import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.menu.AbstractFurnaceTankMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.AbstractFurnaceRecipeBookComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractFurnaceTankScreen<T extends AbstractFurnaceTankMenu> extends AbstractFurnaceScreen<T> {
    private ImageButton recipeButton;
    private float animatedHeight = 0.0F;

    public AbstractFurnaceTankScreen(T menu, AbstractFurnaceRecipeBookComponent recipeBookComponent, Inventory playerInventory, Component title, ResourceLocation texture, ResourceLocation litProgressSprite, ResourceLocation burnProgressSprite) {
        super(menu, recipeBookComponent, playerInventory, title, texture, litProgressSprite, burnProgressSprite);
    }

    @Override
    public void init() {
        super.init();
        this.animatedHeight = 0.0F;
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

        float targetHeight = (this.menu.getLavaStored() / 1000.0F) * 48.0F;
        this.animatedHeight += (targetHeight - this.animatedHeight) * 0.05F;

        int barX = x + 15;
        int currentHeight = Math.round(this.animatedHeight);
        if (currentHeight > 0) {
            TextureAtlasSprite sprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(IClientFluidTypeExtensions.of(Fluids.LAVA).getStillTexture());

            int barY = y + 67 - currentHeight;

            guiGraphics.enableScissor(barX, barY, barX + 4, y + 67);

            for (int ty = y + 19; ty < y + 67; ty += 16) {
                guiGraphics.blit(barX, ty, 0, 16, 16, sprite);
            }

            guiGraphics.disableScissor();
        }
        guiGraphics.fill(barX, y + 19, barX + 1, y + 67, 0x3F000000);
        guiGraphics.fill(barX, y + 19, barX + 4, y + 20, 0x3F000000);
    }
}