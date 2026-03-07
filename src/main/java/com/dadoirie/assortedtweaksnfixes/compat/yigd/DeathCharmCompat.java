package com.dadoirie.assortedtweaksnfixes.compat.yigd;

import com.b1n_ry.yigd.events.YigdEvents;
import com.b1n_ry.yigd.util.DropRule;
import dev.livaco.deathcharm.Config;
import dev.livaco.deathcharm.DeathCharm;
import dev.livaco.deathcharm.PlayerInventory;
import dev.livaco.deathcharm.datacomponents.RemainingUsesComponent;
import dev.livaco.deathcharm.registration.DataComponentsRegistration;
import dev.livaco.deathcharm.registration.ItemRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

public class DeathCharmCompat {

    private static final HashMap<UUID, PlayerInventory> inventoriesToRestore = new HashMap<>();

    public static void init() {
        NeoForge.EVENT_BUS.addListener(DeathCharmCompat::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(DeathCharmCompat::onLivingDrops);
        NeoForge.EVENT_BUS.addListener(DeathCharmCompat::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(DeathCharmCompat::onAdjustDropRule);
    }

    /** Ported onLivingDeath logic */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Inventory inventory = player.getInventory();
        NonNullList<ItemStack> inventoryContents = inventory.items;

        boolean found = false;
        for (ItemStack stack : inventoryContents) {
            if (stack.getItem() != ItemRegistration.DEATH_CHARM.get()) continue;

            if (Config.CHARM_USES.getAsInt() != 0) {
                RemainingUsesComponent remaining = stack.get(DataComponentsRegistration.USES_LEFT.get());
                if (remaining == null) continue;

                int newUses = remaining.usesLeft() - 1;
                stack.set(DataComponentsRegistration.USES_LEFT.get(), new RemainingUsesComponent(newUses));

                if (newUses == 0) stack.setCount(0);
                else if (newUses < 0) {
                    stack.setCount(0);
                    continue;
                }
            }

            found = true;
            break; // only first usable charm triggers
        }

        if (!found) {
            DeathCharm.LOGGER.info("No Death Charm found on player {} [{}].", player.getDisplayName().getString(), player.getStringUUID());
            return;
        }

        PlayerInventory playerInventory = new PlayerInventory(player);
        inventoriesToRestore.put(player.getUUID(), playerInventory);
        DeathCharm.LOGGER.info("{} [{}] had charm, inventory added to list to restore.", player.getDisplayName().getString(), player.getStringUUID());
    }

    /** Ported onLivingDrops logic */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!inventoriesToRestore.containsKey(player.getUUID())) return;

        DeathCharm.LOGGER.info("Prevented drops for {} [{}].", player.getDisplayName().getString(), player.getStringUUID());
        event.getDrops().clear();
    }

    /** Ported onPlayerRespawn logic */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!inventoriesToRestore.containsKey(player.getUUID())) return;

        inventoriesToRestore.get(player.getUUID()).restoreToPlayer(player);
        inventoriesToRestore.remove(player.getUUID());
        DeathCharm.LOGGER.info("Restored inventory for {} [{}].", player.getDisplayName().getString(), player.getStringUUID());
    }

    /** Required bridge method: ensures Yigd respects Death Charm's keep behavior */
    public static void onAdjustDropRule(YigdEvents.AdjustDropRuleEvent event) {
        Player player = event.getDeathContext().player();
        if (!inventoriesToRestore.containsKey(player.getUUID())) return;

        event.getInventoryComponent().handleGraveItems(
                mod -> true,
                (stack, slot, graveItem) -> graveItem.dropRule = DropRule.KEEP
        );
    }
}