package com.dadoirie.assortedtweaksnfixes.mixin.controlling;

import com.blamejared.controlling.client.CustomList;
import com.blamejared.controlling.client.NewKeyBindsList;
import com.dadoirie.assortedtweaksnfixes.client.KeybindHandler;
import com.dadoirie.assortedtweaksnfixes.mixin.controlling.newkeybindslist.NewKeyBindsListKeyEntryAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(NewKeyBindsList.class)
public abstract class NewKeyBindsListMixin {

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void onInit(CallbackInfo ci) {
        KeybindHandler handler = KeybindHandler.getInstance();
        CustomList customList = (CustomList) (Object) this;

        if (customList.allEntries == null) return;

        // Remove hidden keys from allEntries
        customList.allEntries.removeIf(entry -> {
            if (!(entry instanceof NewKeyBindsList.KeyEntry keyEntry)) return false;

            KeyMapping binding = ((NewKeyBindsListKeyEntryAccessor) keyEntry)
                    .assortedtweaksnfixes$getBinding();

            return handler.isKeyHidden(binding.getName())
                    || handler.isKeyHidden(binding.getCategory());
        });

        // Remove empty categories from allEntries (correct type check)
        List<KeyBindsList.Entry> entries = customList.allEntries;
        for (int i = entries.size() - 1; i >= 0; i--) {
            if (!(entries.get(i) instanceof NewKeyBindsList.CategoryEntry)) continue;

            boolean hasKeysAfter = false;

            for (int j = i + 1; j < entries.size(); j++) {
                if (entries.get(j) instanceof NewKeyBindsList.CategoryEntry) break;
                if (entries.get(j) instanceof NewKeyBindsList.KeyEntry) {
                    hasKeysAfter = true;
                    break;
                }
            }

            if (!hasKeysAfter) {
                entries.remove(i);
            }
        }
    }
}