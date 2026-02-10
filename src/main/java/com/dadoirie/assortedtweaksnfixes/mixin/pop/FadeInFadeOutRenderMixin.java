package com.dadoirie.assortedtweaksnfixes.mixin.pop;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pro.mikey.mods.pop.client.pops.FadeInFadeOutRender;
import pro.mikey.mods.pop.data.AnimStage;
import pro.mikey.mods.pop.data.PopData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Mixin to add multiline text support with styling preservation,
 * vertical positioning for MIDDLE/BOTTOM placements, and horizontal
 * text alignment (CENTER/RIGHT) to FadeInFadeOutRender.
 */
@Mixin(FadeInFadeOutRender.class)
public abstract class FadeInFadeOutRenderMixin {
    
    @Unique
    private List<MutableComponent> popTweaks$cachedLines = null;
    
    @Unique
    private List<String> popTweaks$cachedLineStrings = null;
    
    @Unique
    private float popTweaks$lastOpacity = 0.0F;
    
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void popTweaks$renderMultiline(PopData pop, GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        pop.tracker().onRenderFrame();
        var stage = pop.tracker().getStage();
        var currentStageCompletion = pop.tracker().currentStageCompletion();
        
        var delta = deltaTracker.getGameTimeDeltaTicks();
        
        // Fade in and out, smoothly by lerping the opacity
        float opacity = 1.0f;
        if (stage != AnimStage.IDLE) {
            float nextOpacity = stage == AnimStage.IN ? currentStageCompletion / 100.0F : 1.0F - (currentStageCompletion / 100.0F);
            float prevOpacity = popTweaks$lastOpacity;
            popTweaks$lastOpacity = nextOpacity;
            opacity = Mth.lerp(delta, prevOpacity, nextOpacity);
        }
        
        PoseStack pose = graphics.pose();
        pose.pushPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
        
        // Build lines from component, preserving styling (cached)
        if (popTweaks$cachedLines == null) {
            popTweaks$cachedLines = new ArrayList<>();
            popTweaks$cachedLineStrings = new ArrayList<>();
            MutableComponent[] currentLine = {Component.empty()};
            
            pop.content().visit((Style style, String string) -> {
                String[] parts = string.split("\n", -1);
                for (int i = 0; i < parts.length; i++) {
                    if (i > 0) {
                        if (!currentLine[0].getString().isEmpty()) {
                            popTweaks$cachedLines.add(currentLine[0]);
                            popTweaks$cachedLineStrings.add(currentLine[0].getString());
                            currentLine[0] = Component.empty();
                        }
                    }
                    if (!parts[i].isEmpty()) {
                        currentLine[0] = currentLine[0].append(Component.literal(parts[i]).withStyle(style));
                    }
                }
                return Optional.empty();
            }, pop.content().getStyle());
            
            if (!currentLine[0].getString().isEmpty()) {
                popTweaks$cachedLines.add(currentLine[0]);
                popTweaks$cachedLineStrings.add(currentLine[0].getString());
            }
        }
        
        // Draw the text
        int lineHeight = 9;
        var textWidth = 0;
        int textHeight = popTweaks$cachedLines.size() * lineHeight;
        for (var line : popTweaks$cachedLineStrings) {
            textWidth = Math.max(textWidth, Minecraft.getInstance().font.width(line));
        }
        var screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        var screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        var location = pop.placement().location(screenWidth, screenHeight, textWidth);
        int x = location[0];
        int y = location[1];
        
        // Adjust Y for MIDDLE and BOTTOM placements
        if (pop.placement().name().startsWith("MIDDLE")) {
            y = y - (textHeight / 2);
        } else if (pop.placement().name().startsWith("BOTTOM")) {
            y = y - textHeight;
        }
        
        // Render each line
        for (int i = 0; i < popTweaks$cachedLines.size(); i++) {
            var lineWidth = Minecraft.getInstance().font.width(popTweaks$cachedLineStrings.get(i));
            var offsetX = x;
            
            if (pop.placement().name().endsWith("_CENTER")) {
                offsetX = x + (textWidth - lineWidth) / 2;
            } else if (pop.placement().name().endsWith("_RIGHT")) {
                offsetX = x + (textWidth - lineWidth);
            }
            
            int lineY = y + (i * lineHeight);
            graphics.drawString(Minecraft.getInstance().font, popTweaks$cachedLines.get(i), offsetX, lineY, 0xFFFFFFFF);
        }
        
        pose.popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        // Cancel the original render method
        ci.cancel();
    }
}
