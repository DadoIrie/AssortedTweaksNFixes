package com.dadoirie.assortedtweaksnfixes.mixin.accessories;

import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Iterator;

@Mixin(AccessoriesExperimentalScreen.class)
public abstract class RemoveCosmeticButtonMixin {
    @Inject(method = "createSideBarOptions", at = @At("RETURN"))
    private void removeCosmeticButton(CallbackInfoReturnable<FlowLayout> cir) {
        FlowLayout sideBar = cir.getReturnValue();
        if (sideBar != null) {
            // Remove the accessories_toggle_panel component entirely
            removeComponentById(sideBar, "accessories_toggle_panel");
        }
    }
    
    private void removeComponentById(Component parent, String targetId) {
        if (parent instanceof FlowLayout flow) {
            try {
                java.lang.reflect.Field childrenField = FlowLayout.class.getDeclaredField("children");
                childrenField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<Component> mutableChildren = (List<Component>) childrenField.get(flow);
                Iterator<Component> iterator = mutableChildren.iterator();
                while (iterator.hasNext()) {
                    Component child = iterator.next();
                    String childId = child.id() != null ? child.id() : "null";
                    if (targetId.equals(childId)) {
                        iterator.remove();
                        return;
                    }
                    removeComponentById(child, targetId);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
    }
}