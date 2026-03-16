# Assorted Tweaks & Fixes

A mod that applies various tweaks and fixes to supported mods. Each tweak is toggleable via config file.


## Puzzles Lib Compat
`puzzleslib.ConfigTranslationsManagerMixin` (0.9.6-beta)<br>
- Fixes raw translation keys in config screens for mods that depend on Puzzles Lib when Server Translations API is present.

## Condiments & Dye Depot Compat
(0.9.0-beta)<br>

Adds compatibility between **Condiments** and **Dye Depot** colors. Generates colored crate variants using Dye Depot's color palette.

## Refined Storage & Dye Depot Compat
(0.8.0-beta)<br>

Adds compatibility between **Refined Storage** and **Dye Depot** colors. Now there are more variations for all the colored blocks in Refined Storage (Controller, Grid, Autocrafter, Cables, etc.) as well more colored RS cables.

## Mekanism & Dye Depot Pigmentation Compat 
(0.7.0-beta)<br>

Adds compatibility between **Mekanism** and **Dye Depot** colors. The **Pigment Extractor** and **Painter** now work not only with vanilla blocks like Mekanism does, but also with blocks colored using Dye Depot’s palette.

- This isn’t a mixin, so it doesn’t require a toggle—it’s a full compat feature.
- Recipes and interactions are fully conditional; nothing breaks or produces invalid recipes.
- The **Pigment Mixer** includes almost 600 combinations now.s
- Future updates will expand compat to more vanilla and non-vanilla blocks for the Painter and Pigment Extractor.

## Death Charm & You're in Grave Danger Compat

`deathcharm.RestoreInventoryEventsMixin` (0.6.2-beta)<br>
Death Charm works correctly when **You're in Grave Danger** is installed and doesn't duplicate the inventory.

## Diagonal Blocks

`diagonalblocks.DiagonalBlockHandlerMixin` (0.5.0-beta)<br>
Ensures diagonal walls keep their block tags correctly and prevents crashes.  
**Special Note:** This mixin only applies if **Additional Placements**, **Condiments**, **Quark**, and **Diagonal Walls** (changes then are applied to Diagonal Walls, Diagonal Fences and Diagonal Windows) are all loaded. Running all three mods together without it crashes the game.

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
