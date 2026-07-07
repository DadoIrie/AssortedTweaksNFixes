package com.dadoirie.assortedtweaksnfixes.mixin.controlling.newkeybindslistkeyentry;

import com.blamejared.controlling.api.DisplayMode;
import com.dadoirie.assortedtweaksnfixes.client.KeybindHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.function.Predicate;

@Mixin(DisplayMode.class)
public class DisplayModeMixin {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void replaceConflictingPredicate(CallbackInfo ci) {
        try {
            Field predicateField = DisplayMode.class.getDeclaredField("predicate");
            predicateField.setAccessible(true);

            Predicate<KeyBindsList.Entry> newPredicate = entry -> {
                if (!(entry instanceof com.blamejared.controlling.api.entries.IKeyEntry keyEntry)) return false;

                KeyMapping current = keyEntry.getKey();
                if (current.isUnbound()) return false;

                KeybindHandler handler = KeybindHandler.getInstance();
                if (handler.isKeyHidden(current.getName()) || handler.isKeyHidden(current.getCategory())) {
                    return false;
                }

                for (KeyMapping other : Minecraft.getInstance().options.keyMappings) {
                    if (other == current || other.isUnbound()) continue;
                    if (handler.isKeyHidden(other.getName()) || handler.isKeyHidden(other.getCategory())) continue;

                    boolean sameKey = current.getKey() == other.getKey();
                    boolean sameModifier = current.getKeyModifier() == other.getKeyModifier();
                    boolean contextsOverlap = current.getKeyConflictContext().conflicts(other.getKeyConflictContext())
                            || other.getKeyConflictContext().conflicts(current.getKeyConflictContext());

                    if (sameKey && sameModifier && contextsOverlap) {
                        return true;
                    }
                }
                return false;
            };

            // Write to the final field of the CONFLICTING enum constant
            predicateField.set(DisplayMode.CONFLICTING, newPredicate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}