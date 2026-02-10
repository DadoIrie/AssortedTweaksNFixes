# Assorted Tweaks & Fixes

A mod that applies various tweaks and fixes to supported mods. Each tweak is toggleable via config file.

## Supported Mods

### Accessories
Tweaks for the Accessories mod's Experimental menu.

| Toggle | Description |
|-|-|
| `accessories.InventoryKeyMixin` | Pressing the Accessories key while another screen like crafting table is open closes it to behave like vanilla inventory |
| `accessories.RemoveCraftingGridButtonMixin` | Removes the crafting grid toggle button and always shows the crafting grid |
| `accessories.RemoveCosmeticButtonMixin` | Removes the cosmetic toggle button from the sidebar *might add a separate mechanic* |
| `accessories.RemoveCosmeticArmorSlotsMixin` | Removes cosmetic armor slots from the UI entirely *same here* |
| `accessories.RemoveBackButtonMixin` | Removes the back button that switches to base inventory |
| `accessories.MovePlayerModelRightMixin` | Shifts the player model in the entity view slightly to the right |

### Etched
Tweaks for the Etched mod.

| Toggle | Description |
|-|-|
| `etched.EtchingScreenMixin` | Prevents empty discs from invalid local sound IDs in the etching table |

### Traveler's Backpack
Tweaks for Traveler's Backpack mod (requires Etched).

| Toggle | Description |
|-|-|
| `travelersbackpack.JukeboxWidgetMixin` | Enables playing etched discs in the backpack jukebox upgrade |

## Notes

- Tweaks won't apply/load if the target mod isn't present
- Mainly meant for my modpack but can be used standalone
- All tweaks are toggleable via the generated config file
- Tweaks vary from small adjustments to substantial additions or/and removals
- Using any tweak is at your own risk since it relies on mixins
