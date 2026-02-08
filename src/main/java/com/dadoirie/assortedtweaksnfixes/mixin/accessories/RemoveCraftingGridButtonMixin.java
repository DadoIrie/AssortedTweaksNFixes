package com.dadoirie.assortedtweaksnfixes.mixin.accessories;

import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AccessoriesExperimentalScreen.class)
public abstract class RemoveCraftingGridButtonMixin {
    @Inject(method = "build", at = @At("TAIL"))
    private void RemoveCraftingGridButton(FlowLayout rootComponent, CallbackInfo ci) {
        accessoriesTweaks$debugRemoveButtonById(rootComponent, "crafting_grid_button");
    }
    @Unique
    private void accessoriesTweaks$debugRemoveButtonById(Component parent, String targetId) {
        if (parent instanceof FlowLayout flow) {
            try {
                java.lang.reflect.Field childrenField = FlowLayout.class.getDeclaredField("children");
                childrenField.setAccessible(true);
                @SuppressWarnings("unchecked")
                java.util.List<Component> mutableChildren = (java.util.List<Component>) childrenField.get(flow);
                int i = 0;
                java.util.Iterator<Component> iterator = mutableChildren.iterator();
                while (iterator.hasNext()) {
                    Component child = iterator.next();
                    String childId = child.id() != null ? child.id() : "null";
                    if (targetId.equals(childId)) {
                        iterator.remove();
                        FlowLayout placeholder = Containers.horizontalFlow(Sizing.fixed(18), Sizing.fixed(18));
                        placeholder.id("placeholder_crafting");
                        mutableChildren.add(i, placeholder);
                        return;
                    }
                    accessoriesTweaks$debugRemoveButtonById(child, targetId);
                    i++;
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
    }
    @Inject(method = "showCraftingGrid", at = @At("HEAD"), cancellable = true)
    private void alwaysShowCraftingGrid(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
