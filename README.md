# Assorted Tweaks & Fixes

A mod that applies various tweaks and fixes to supported mods. Each tweak is toggleable via config file.

## Accessories

`accessories.InventoryKeyMixin`  (0.4.3-beta)<br>
Pressing the Accessories key while another screen like crafting table is open closes it to behave like vanilla inventory

`accessories.RemoveCraftingGridButtonMixin`  (0.4.3-beta)<br>
Removes the crafting grid toggle button and always shows the crafting grid

`accessories.RemoveCosmeticButtonMixin`  (0.4.3-beta)<br>
Removes the cosmetic toggle button from the sidebar.<br>Shift-click mechanics added: `Shift`+`Right-click` toggles visibility, `Shift`+`Left-click` toggles cosmetic/regular slot view

`accessories.RemoveCosmeticArmorSlotsMixin`  (0.4.3-beta)<br>
Removes cosmetic armor slots from the UI entirely

`accessories.RemoveBackButtonMixin`  (0.4.3-beta)<br>
Removes the back button that switches to base inventory

`accessories.MovePlayerModelRightMixin` (0.4.3-beta)<br>
Shifts the player model in the entity view slightly to the right


## Etched

`etched.EtchingScreenMixin` (0.4.3-beta)<br>
Prevents empty discs from invalid local sound IDs in the etching table


## Traveler's Backpack

`travelersbackpack.JukeboxWidgetMixin` (0.4.3-beta)<br>
Enables playing etched discs in the backpack jukebox upgrade (requires Etched)


## Pop

`pop.FadeInFadeOutRenderMixin` (0.4.3-beta)<br>
Adds multiline text support


## Notes

- Tweaks won't apply/load if the target mod isn't present  
- Mainly meant for my modpack but can be used standalone  
- All tweaks are toggleable via the generated config file  
- Tweaks vary from small adjustments to substantial additions/removals  
- Using any tweak is at your own risk since it relies on mixins
