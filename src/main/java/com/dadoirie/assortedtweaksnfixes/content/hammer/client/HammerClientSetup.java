package com.dadoirie.assortedtweaksnfixes.content.hammer.client;

import com.dadoirie.assortedtweaksnfixes.content.hammer.HammerFeature;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.createmod.catnip.lang.FontHelper;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class HammerClientSetup {

    private HammerClientSetup() {
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> TooltipModifier.REGISTRY.register(HammerFeature.HAMMER.get(),
                new ItemDescription.Modifier(HammerFeature.HAMMER.get(), FontHelper.Palette.STANDARD_CREATE)));
    }

    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(SimpleCustomRenderer.create(HammerFeature.HAMMER.get(), new HammerItemRenderer()),
                HammerFeature.HAMMER.get());
    }
}