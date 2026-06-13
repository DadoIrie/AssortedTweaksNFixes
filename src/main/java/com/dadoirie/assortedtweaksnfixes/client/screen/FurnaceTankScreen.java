package com.dadoirie.assortedtweaksnfixes.client.screen;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.menu.FurnaceTankMenu;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FurnaceTankScreen extends AbstractFurnaceTankScreen<FurnaceTankMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AssortedTweaksNFixesConstants.MOD_ID, "textures/gui/container/furnace_tank.png");
    private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    public FurnaceTankScreen(FurnaceTankMenu menu, Inventory playerInventory, Component title) {
        super(menu, new SmeltingRecipeBookComponent(), playerInventory, title, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
    }
}