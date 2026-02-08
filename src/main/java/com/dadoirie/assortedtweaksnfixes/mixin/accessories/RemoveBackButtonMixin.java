package com.dadoirie.assortedtweaksnfixes.mixin.accessories;

import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mixin(AccessoriesExperimentalScreen.class)
public abstract class RemoveBackButtonMixin {
    @Inject(method = "build", at = @At("TAIL"))
    private void removeBackButton(FlowLayout rootComponent, CallbackInfo ci) {
        try {
            Field childrenField = FlowLayout.class.getDeclaredField("children");
            childrenField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Component> children = (List<Component>) childrenField.get(rootComponent);
            removeBackButton(children, childrenField);
        } catch (Exception ignored) {}
    }

    private void removeBackButton(List<Component> children, Field flowChildrenField) {
        for (Component child : children) {
            if ("armor_entity_layout".equals(child.id()) && child instanceof FlowLayout flowLayout) {
                try {
                    @SuppressWarnings("unchecked")
                    List<Component> armorChildren = (List<Component>) flowChildrenField.get(flowLayout);
                    for (Component armorChild : armorChildren) {
                        if (armorChild instanceof StackLayout stackLayout) {
                            // Get StackLayout's children field directly
                            Field stackField = StackLayout.class.getDeclaredField("children");
                            stackField.setAccessible(true);
                            @SuppressWarnings("unchecked")
                            List<Component> stackChildren = (List<Component>) stackField.get(stackLayout);
                            if (stackChildren.size() > 3 && stackChildren.get(3) instanceof ButtonComponent) {
                                List<Component> newChildren = new ArrayList<>(stackChildren);
                                newChildren.remove(3);
                                stackField.set(stackLayout, newChildren);
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }
            if (child instanceof FlowLayout flowLayout) {
                try {
                    @SuppressWarnings("unchecked")
                    List<Component> childChildren = (List<Component>) flowChildrenField.get(flowLayout);
                    removeBackButton(childChildren, flowChildrenField);
                } catch (Exception ignored) {}
            }
        }
    }
}
