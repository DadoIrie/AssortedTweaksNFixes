package com.dadoirie.assortedtweaksnfixes.mixin.accessories.removecosmeticarmorslots;

import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import io.wispforest.accessories.client.gui.components.InventoryEntityComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.ParentComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AccessoriesExperimentalScreen.class)
public abstract class MovePlayerModelRightMixin {

    @Inject(method = "build", at = @At("TAIL"))
    private void movePlayerModelRight(FlowLayout rootComponent, CallbackInfo ci) {
        movePlayerModelRightRecursively(rootComponent);
    }

    private void movePlayerModelRightRecursively(Component component) {
        if (component instanceof InventoryEntityComponent<?> entityComponent && "entity_rendering_component".equals(component.id())) {
            // Set xOffset directly on the entity component
            entityComponent.xOffset = 0.25f;
            return;
        }

        if (component instanceof ParentComponent parent) {
            for (Component child : List.copyOf(parent.children())) {
                movePlayerModelRightRecursively(child);
            }
        }
    }
}
