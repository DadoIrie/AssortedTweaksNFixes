package com.dadoirie.assortedtweaksnfixes.mixin.minecraft;

import com.dadoirie.assortedtweaksnfixes.client.KeybindHandler;
import com.dadoirie.assortedtweaksnfixes.mixin.minecraft.keybindslist.KeyEntryAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(KeyBindsList.class)
public abstract class KeyBindsListMixin extends ContainerObjectSelectionList<KeyBindsList.Entry> {

    public KeyBindsListMixin(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        KeybindHandler handler = KeybindHandler.getInstance();

        this.children().removeIf(entry -> {
            if (!(entry instanceof KeyBindsList.KeyEntry keyEntry)) return false;
            KeyMapping binding = ((KeyEntryAccessor) keyEntry).assortedtweaksnfixes$getBinding();
            return handler.isKeyHidden(binding.getName())
                    || handler.isKeyHidden(binding.getCategory());
        });

        List<KeyBindsList.Entry> entries = this.children();
        for (int i = entries.size() - 1; i >= 0; i--) {
            if (!(entries.get(i) instanceof KeyBindsList.CategoryEntry)) continue;
            if (i == entries.size() - 1 || entries.get(i + 1) instanceof KeyBindsList.CategoryEntry) {
                entries.remove(i);
            }
        }
    }
}