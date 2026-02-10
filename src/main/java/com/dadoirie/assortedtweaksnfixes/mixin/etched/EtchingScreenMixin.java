package com.dadoirie.assortedtweaksnfixes.mixin.etched;

import gg.moonflower.etched.api.record.TrackData;
import gg.moonflower.etched.client.screen.EtchingScreen;
import gg.moonflower.etched.common.network.play.SetUrlPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EtchingScreen.class)
public abstract class EtchingScreenMixin {
    @Shadow private EditBox url;

    @Redirect(
        method = "containerTick",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/String;)Lgg/moonflower/etched/common/network/play/SetUrlPacket;"
        )
    )
    private SetUrlPacket redirectSetUrlPacket(String urlValue) {
        if (TrackData.isLocalSound(urlValue)) {
            ResourceLocation soundId = ResourceLocation.tryParse(urlValue);
            if (soundId != null && Minecraft.getInstance().getSoundManager().getSoundEvent(soundId) != null) {
                return new SetUrlPacket(urlValue);
            }
            return new SetUrlPacket("");
        }
        return new SetUrlPacket(urlValue);
    }
}
