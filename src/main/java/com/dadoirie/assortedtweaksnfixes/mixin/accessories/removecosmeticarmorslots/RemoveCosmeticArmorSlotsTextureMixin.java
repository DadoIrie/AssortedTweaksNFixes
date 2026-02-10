package com.dadoirie.assortedtweaksnfixes.mixin.accessories.removecosmeticarmorslots;

import io.wispforest.accessories.Accessories;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Redirects full_armor.png to modified_full_armor.png for the entity view texture.
 * Coupled with RemoveCosmeticArmorSlotsMixin - disabled when that mixin is disabled.
 */
@Mixin(Accessories.class)
public class RemoveCosmeticArmorSlotsTextureMixin {
    
    @Redirect(
        method = "of(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
        )
    )
    private static ResourceLocation redirectTexturePath(String namespace, String path) {
        if (path.contains("full_armor.png")) {
            path = path.replace("full_armor.png", "modified_full_armor.png");
        }
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}
