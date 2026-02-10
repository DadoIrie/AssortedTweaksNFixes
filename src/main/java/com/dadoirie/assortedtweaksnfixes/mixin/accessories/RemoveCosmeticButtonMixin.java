package com.dadoirie.assortedtweaksnfixes.mixin.accessories;

import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.client.gui.AccessoriesExperimentalScreen;
import io.wispforest.accessories.client.gui.components.ComponentUtils;
import io.wispforest.accessories.client.gui.components.ExtendedCollapsibleContainer;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AccessoriesExperimentalScreen.class)
public abstract class RemoveCosmeticButtonMixin {

    @Inject(method = "createSideBarOptions", at = @At("RETURN"))
    private void removeCosmeticButton(CallbackInfoReturnable<FlowLayout> cir) {
        FlowLayout sideBarHolder = cir.getReturnValue();
        if (sideBarHolder == null) return;
        
        FlowLayout togglePanel = sideBarHolder.childById(FlowLayout.class, "accessories_toggle_panel");
        if (togglePanel == null) return;
        
        List<Component> children = togglePanel.children();
        if (!children.isEmpty()) {
            Component cosmeticButton = children.get(0);
            togglePanel.removeChild(cosmeticButton);
        }
        
        // Replace the ExtendedCollapsibleContainer with just its content
        // This removes the expand/collapse functionality and prevents layout shifts
        ExtendedCollapsibleContainer groupFilter = togglePanel.childById(ExtendedCollapsibleContainer.class, "group_filter_component");
        if (groupFilter != null) {
            List<Component> collapsibleChildren = groupFilter.children();
            // collapsible structure: index 0 = titleLayout (arrow), index 1 = contentLayout (filter buttons)
            if (collapsibleChildren.size() >= 2) {
                Component contentLayout = collapsibleChildren.get(1);
                // Add top margin to move the filter buttons down slightly
                contentLayout.margins(Insets.top(2));
                // Remove the collapsible from the panel
                togglePanel.removeChild(groupFilter);
                // Add the content directly (bypassing the collapsible wrapper)
                togglePanel.child(contentLayout);
            }
        }
        
        togglePanel.surface((ctx, component) -> {
            if (component.children().size() > 1) {
                ComponentUtils.getPanelSurface().and(ComponentUtils.getPanelWithInset(6)).draw(ctx, component);
            } else {
                ctx.blit(
                    Accessories.of("textures/gui/theme/" + ComponentUtils.checkMode("light", "dark") + "/modified_toggle_background.png"),
                    component.x(),
                    component.y(),
                    component.width(),
                    component.height(),
                    0,
                    0,
                    36,
                    36,
                    36,
                    36
                );
            }
        });
    }
}
