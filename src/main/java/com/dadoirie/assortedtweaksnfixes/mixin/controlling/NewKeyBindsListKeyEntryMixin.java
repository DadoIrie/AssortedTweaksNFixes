package com.dadoirie.assortedtweaksnfixes.mixin.controlling;

import com.blamejared.controlling.client.NewKeyBindsList;
import com.dadoirie.assortedtweaksnfixes.mixin.controlling.newkeybindslistkeyentry.NewKeyBindsListAccessor;
import com.dadoirie.assortedtweaksnfixes.client.KeybindHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NewKeyBindsList.KeyEntry.class)
public abstract class NewKeyBindsListKeyEntryMixin {

    @Shadow @Final
    private KeyMapping key;

    @Shadow
    private boolean hasCollision;

    @Shadow @Final
    private Button btnChangeKeyBinding;

    @Shadow @Final
    NewKeyBindsList this$0;

    @Inject(method = "refreshEntry", at = @At("TAIL"))
    private void forceStrictCollision(CallbackInfo ci) {
        KeybindHandler handler = KeybindHandler.getInstance();

        if (handler.isKeyHidden(key.getName()) || handler.isKeyHidden(key.getCategory())) {
            this.hasCollision = false;
            return;
        }

        this.hasCollision = false;
        MutableComponent duplicates = Component.empty();
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
                    if (this.hasCollision) duplicates.append(", ");
                    this.hasCollision = true;
                    duplicates.append(other.getDisplayName());
                }
            }
        }

        if (this.hasCollision) {
            this.btnChangeKeyBinding.setMessage(
                    Component.literal("[ ")
                            .append(this.key.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.WHITE))
                            .append(" ]")
                            .withStyle(ChatFormatting.RED)
            );
            MutableComponent tooltip = Component.translatable(this.key.getCategory());
            tooltip.append(CommonComponents.NEW_LINE);
            tooltip.append(Component.translatable("controls.keybinds.duplicateKeybinds", duplicates));
            this.btnChangeKeyBinding.setTooltip(Tooltip.create(tooltip));
        } else {
            this.btnChangeKeyBinding.setMessage(this.key.getTranslatedKeyMessage());
            this.btnChangeKeyBinding.setTooltip(Tooltip.create(Component.translatable(this.key.getCategory())));
        }

        if (((NewKeyBindsListAccessor) this$0).atfnf_getControlsScreen().selectedKey == this.key) {
            this.btnChangeKeyBinding.setMessage(
                    Component.literal("> ")
                            .append(this.btnChangeKeyBinding.getMessage().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE))
                            .append(" <")
                            .withStyle(ChatFormatting.YELLOW)
            );
        }
    }
}