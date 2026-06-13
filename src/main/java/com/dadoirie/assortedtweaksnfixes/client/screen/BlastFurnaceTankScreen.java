package com.dadoirie.assortedtweaksnfixes.client.screen;

import com.dadoirie.assortedtweaksnfixes.AssortedTweaksNFixesConstants;
import com.dadoirie.assortedtweaksnfixes.content.furnace_tank.menu.BlastFurnaceTankMenu;
import net.minecraft.client.gui.screens.recipebook.BlastingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BlastFurnaceTankScreen extends AbstractFurnaceTankScreen<BlastFurnaceTankMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            AssortedTweaksNFixesConstants.MOD_ID, "textures/gui/container/blast_furnace_tank.png");
    private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/blast_furnace/lit_progress");
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/blast_furnace/burn_progress");

    public BlastFurnaceTankScreen(BlastFurnaceTankMenu menu, Inventory playerInventory, Component title) {
        super(menu, new BlastingRecipeBookComponent(), playerInventory, title, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
    }
}