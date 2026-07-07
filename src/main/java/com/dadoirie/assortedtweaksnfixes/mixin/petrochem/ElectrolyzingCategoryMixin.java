package com.dadoirie.assortedtweaksnfixes.mixin.petrochem;

import com.dadoirie.assortedtweaksnfixes.compat.electroenergetics.ElectrolyzerElectricDevice;
import com.george_vi.electroenergetics.config.CEEConfigs;
import io.github.hadron13.petrochem.blocks.electrolyzer.ElectrolyzingRecipe;
import io.github.hadron13.petrochem.compat.jei.category.ElectrolyzingCategory;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Replaces the "100Fe/tick" line in the JEI electrolyzing category with the
 * electric requirement at the base (50%) dial: the recipe's power draw in
 * watts, at the machine's rated voltage. Watts are derived live from the
 * recipe's energy value through CEE's own FE<->watt conversion config.
 */
@Mixin(value = ElectrolyzingCategory.class, remap = false)
public abstract class ElectrolyzingCategoryMixin {

    @ModifyVariable(method = "draw", at = @At("STORE"), name = "powerString")
    private String atf_showElectricRequirement(String powerString, ElectrolyzingRecipe recipe, IRecipeSlotsView iRecipeSlotsView,
                                               GuiGraphics graphics, double mouseX, double mouseY) {
        double watts = recipe.requiredEnergy * CEEConfigs.server().wattFeTConversionRate.get();
        String power = watts >= 1000
                ? String.format("%.1fkW", watts / 1000d)
                : (int) watts + "W";
        return power + " @ " + (int) ElectrolyzerElectricDevice.RATED_VOLTAGE + "V";
    }
}