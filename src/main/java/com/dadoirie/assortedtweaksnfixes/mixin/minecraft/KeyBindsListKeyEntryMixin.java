package com.dadoirie.assortedtweaksnfixes.mixin.minecraft;

import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.keybindslistkeyentry.KeyBindsListAccessor;
import com.dadoirie.assortedtweaksnfixes.client.KeybindHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyBindsList.KeyEntry.class)
public abstract class KeyBindsListKeyEntryMixin {

    @Shadow @Final
    private KeyMapping key;

    @Shadow
    private boolean hasCollision;

    @Shadow @Final
    private Button changeButton;

    @Shadow @Final
    KeyBindsList this$0;

    @Inject(method = "refreshEntry", at = @At("TAIL"))
    private void forceStrictCollision(CallbackInfo ci) {
        KeybindHandler handler = KeybindHandler.getInstance();

        if (handler.isKeyHidden(key.getName()) || handler.isKeyHidden(key.getCategory())) {
            this.hasCollision = false;
            return;
        }

        boolean collision = false;
        MutableComponent conflictingKeys = Component.empty();
        if (!key.isUnbound()) {
            for (KeyMapping other : Minecraft.getInstance().options.keyMappings) {
                if (handler.isKeyHidden(other.getName()) || handler.isKeyHidden(other.getCategory())) {
                    continue;
                }
                if (other != key
                        && key.getKey() == other.getKey()
                        && key.getKeyModifier() == other.getKeyModifier()
                        && (key.getKeyConflictContext().conflicts(other.getKeyConflictContext())
                        || other.getKeyConflictContext().conflicts(key.getKeyConflictContext()))) {
                    if (collision) conflictingKeys.append(", ");
                    collision = true;
                    conflictingKeys.append(other.getDisplayName());
                }
            }
        }

        this.hasCollision = collision;
        if (collision) {
            this.changeButton.setMessage(
                    Component.literal("[ ")
                            .append(this.key.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.WHITE))
                            .append(" ]")
                            .withStyle(ChatFormatting.RED)
            );
            this.changeButton.setTooltip(
                    Tooltip.create(Component.translatable("controls.keybinds.duplicateKeybinds", conflictingKeys))
            );
        } else {
            this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
            this.changeButton.setTooltip(null);
        }

        if (((KeyBindsListAccessor) this$0).atfnf_getKeyBindsScreen().selectedKey == this.key) {
            this.changeButton.setMessage(
                    Component.literal("> ")
                            .append(this.changeButton.getMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                            .append(" <")
                            .withStyle(ChatFormatting.YELLOW)
            );
        }
    }
}