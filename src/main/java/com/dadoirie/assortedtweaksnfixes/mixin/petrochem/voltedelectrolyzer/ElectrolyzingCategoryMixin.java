package com.dadoirie.assortedtweaksnfixes.mixin.petrochem.voltedelectrolyzer;

import com.dadoirie.assortedtweaksnfixes.compat.electroenergetics.ElectrolyzerElectricDevice;
import io.github.hadron13.petrochem.blocks.electrolyzer.ElectrolyzingRecipe;
import io.github.hadron13.petrochem.compat.jei.category.ElectrolyzingCategory;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ElectrolyzingCategory.class, remap = false)
public abstract class ElectrolyzingCategoryMixin {

    @ModifyVariable(method = "draw", at = @At("STORE"), name = "powerString")
    private String atf_showElectricRequirement(String powerString, ElectrolyzingRecipe recipe, IRecipeSlotsView iRecipeSlotsView,
                                               GuiGraphics graphics, double mouseX, double mouseY) {
        ElectrolyzerElectricDevice.Electric electric = (ElectrolyzerElectricDevice.Electric) recipe;
        double kw = electric.atnf$kilowatts();
        return kw >= 1.0
                ? String.format("%.1fkW", kw)
                : (int) Math.round(kw * 1000) + "W";
    }

    @Inject(method = "draw", at = @At("TAIL"))
    private void atf_showVoltageRange(ElectrolyzingRecipe recipe, IRecipeSlotsView iRecipeSlotsView,
                                      GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        ElectrolyzerElectricDevice.Electric electric = (ElectrolyzerElectricDevice.Electric) recipe;
        double rated = electric.atnf$voltage();
        int minVoltage = ElectrolyzerElectricDevice.minVoltage(rated);
        int maxVoltage = ElectrolyzerElectricDevice.maxVoltage(rated);

        Component range = Component.literal("@ " + minVoltage + "V - " + maxVoltage + "V");
        graphics.drawString(Minecraft.getInstance().font, range, 10, 30, 0xffffff);
    }
}