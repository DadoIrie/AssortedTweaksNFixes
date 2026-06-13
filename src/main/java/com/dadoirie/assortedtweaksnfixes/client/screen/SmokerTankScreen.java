package com.dadoirie.assortedtweaksnfixes.client.screen;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.menu.SmokerTankMenu;
import net.minecraft.client.gui.screens.recipebook.SmokingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SmokerTankScreen extends AbstractFurnaceTankScreen<SmokerTankMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AssortedTweaksNFixesConstants.MOD_ID, "textures/gui/container/smoker_tank.png");
    private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/smoker/lit_progress");
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/smoker/burn_progress");

    public SmokerTankScreen(SmokerTankMenu menu, Inventory playerInventory, Component title) {
        super(menu, new SmokingRecipeBookComponent(), playerInventory, title, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
    }
}