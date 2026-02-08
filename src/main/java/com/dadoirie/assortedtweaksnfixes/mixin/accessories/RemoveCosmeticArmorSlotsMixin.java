package com.dadoirie.assortedtweaksnfixes.mixin.accessories;

import io.wispforest.accessories.api.menu.AccessoriesBasedSlot;
import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import io.wispforest.accessories.menu.ArmorSlotTypes;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.ParentComponent;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AccessoriesExperimentalScreen.class)
public abstract class RemoveCosmeticArmorSlotsMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("RemoveCosmeticArmorSlots");

    @Inject(method = "enableSlot(Lnet/minecraft/world/inventory/Slot;)V", at = @At("HEAD"), cancellable = true)
    private void logAndPreventArmorCosmeticSlots(Slot slot, CallbackInfo ci) {
        if (slot instanceof AccessoriesBasedSlot accessoriesSlot) {
            String slotName = accessoriesSlot.slotName();
            boolean isArmor = ArmorSlotTypes.isArmorType(slotName);
            boolean isCosmetic = accessoriesSlot.isCosmeticSlot();
            
            // Cancel cosmetic armor slots
            if (isCosmetic && isArmor) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "build", at = @At("TAIL"))
    private void onBuildComplete(FlowLayout rootComponent, CallbackInfo ci) {
        removeCosmeticArmorSlots(rootComponent, 0, null, null);
    }
    
    private void removeCosmeticArmorSlots(Component component, int depth, @Nullable ParentComponent parent, @Nullable ParentComponent grandparent) {
        // Check if this is an ExtendedSlotComponent that is a cosmetic armor slot
        if (component instanceof AccessoriesExperimentalScreen.ExtendedSlotComponent) {
            AccessoriesExperimentalScreen.ExtendedSlotComponent slotComp = (AccessoriesExperimentalScreen.ExtendedSlotComponent) component;
            Slot mcSlot = slotComp.slot();
            if (mcSlot instanceof AccessoriesBasedSlot) {
                AccessoriesBasedSlot accSlot = (AccessoriesBasedSlot) mcSlot;
                
                if (accSlot.isCosmeticSlot() && ArmorSlotTypes.isArmorType(accSlot.slotName())) {
                    if (parent != null && grandparent != null) {
                        grandparent.removeChild(parent);
                    }
                }
            }
        }
        
        if (component instanceof ParentComponent) {
            ParentComponent currentParent = (ParentComponent) component;
            for (Component child : List.copyOf(currentParent.children())) {
                removeCosmeticArmorSlots(child, depth + 1, currentParent, parent);
            }
        }
    }
}
