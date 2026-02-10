package com.dadoirie.assortedtweaksnfixes.mixin.accessories.removecosmeticbutton;

import io.wispforest.accessories.api.menu.AccessoriesBasedSlot;
import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import io.wispforest.accessories.networking.AccessoriesNetworking;
import io.wispforest.accessories.networking.server.SyncCosmeticToggle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(AbstractContainerScreen.class)
public class ShiftRightClickToggleMixin {

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void assortedtweaksnfixes$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!Screen.hasShiftDown()) {
            return;
        }
        
        AbstractContainerScreen<?> self = (AbstractContainerScreen<?>) (Object) this;
        
        if (!self.getMenu().getCarried().isEmpty()) {
            return;
        }
        
        Slot hoveredSlot = self.getSlotUnderMouse();
        if (hoveredSlot == null) {
            return;
        }
        
        if (!(hoveredSlot instanceof AccessoriesBasedSlot accessorySlot)) {
            return;
        }
        
        if (button == 0) {
            if (self instanceof AccessoriesExperimentalScreen experimentalScreen) {
                boolean newState = !experimentalScreen.showCosmeticState();
                experimentalScreen.showCosmeticState(newState);
                
                experimentalScreen.rebuildAccessoriesComponent();
            }
            cir.setReturnValue(true);
            return;
        }
        
        if (button == 1) {
            var slotType = accessorySlot.slotType();
            if (slotType == null) {
                cir.setReturnValue(true);
                return;
            }
            
            int slotIndex = accessorySlot.getContainerSlot();
            
            AccessoriesNetworking.sendToServer(SyncCosmeticToggle.of(null, slotType, slotIndex));
            
            cir.setReturnValue(true);
        }
    }
    
    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void assortedtweaksnfixes$onRenderTooltip(CallbackInfo ci) {
        if (!Screen.hasShiftDown()) {
            return;
        }
        
        AbstractContainerScreen<?> self = (AbstractContainerScreen<?>) (Object) this;
        
        if (!self.getMenu().getCarried().isEmpty()) {
            return;
        }
        
        Slot hoveredSlot = self.getSlotUnderMouse();
        if (hoveredSlot instanceof AccessoriesBasedSlot) {
            ci.cancel();
        }
    }
}
