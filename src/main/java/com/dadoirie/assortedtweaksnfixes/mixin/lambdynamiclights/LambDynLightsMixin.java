package com.dadoirie.assortedtweaksnfixes.mixin.lambdynamiclights;

import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.resource.item.ItemLightSources;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LambDynLights.class)
public abstract class LambDynLightsMixin {

    @Redirect(
            method = "getLivingEntityLuminanceFromItems",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/lambdaurora/lambdynlights/resource/item/ItemLightSources;getLuminance(Lnet/minecraft/world/item/ItemStack;Z)I"
            )
    )
    private static int atf_suppressHeldUnlitCampfireLight(ItemLightSources itemLightSources, ItemStack stack, boolean submergedInWater) {
        if (ModList.get().isLoaded("unlitcampfire") && atnf$isCampfire(stack)) {
            return 0;
        }

        return itemLightSources.getLuminance(stack, submergedInWater);
    }

    @Unique
    private static boolean atnf$isCampfire(ItemStack stack) {
        return stack.is(Items.CAMPFIRE) || stack.is(Items.SOUL_CAMPFIRE);
    }
}