package com.dadoirie.assortedtweaksnfixes.compat.yigd;

import com.b1n_ry.yigd.components.GraveComponent;
import com.b1n_ry.yigd.data.DeathInfoManager;
import com.b1n_ry.yigd.data.GraveStatus;
import com.dadoirie.assortedtweaksnfixes.mixin.ConditionalMixinPlugin;
import dev.livaco.deathcharm.Config;
import dev.livaco.deathcharm.DeathCharm;
import dev.livaco.deathcharm.datacomponents.RemainingUsesComponent;
import dev.livaco.deathcharm.registration.DataComponentsRegistration;
import dev.livaco.deathcharm.registration.ItemRegistration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class DeathCharmCompat {

    private static final HashSet<UUID> playersWithCharm = new HashSet<>();

    public static void init() {
        if (ModList.get().isLoaded("iygd") && ModList.get().isLoaded("deathcharm") && ConditionalMixinPlugin.isMixinEnabled("deathcharm.RestoreInventoryEventsMixin")) {
            NeoForge.EVENT_BUS.addListener(DeathCharmCompat::onLivingDeath);
            NeoForge.EVENT_BUS.addListener(DeathCharmCompat::onPlayerRespawn);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        boolean found = false;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() != ItemRegistration.DEATH_CHARM.get()) continue;

            if (Config.CHARM_USES.getAsInt() != 0) {
                RemainingUsesComponent remaining = stack.get(DataComponentsRegistration.USES_LEFT.get());
                if (remaining == null) continue;

                int newUses = remaining.usesLeft() - 1;
                stack.set(DataComponentsRegistration.USES_LEFT.get(), new RemainingUsesComponent(newUses));

                if (newUses == 0) {
                    stack.setCount(0);
                } else if (newUses < 0) {
                    stack.setCount(0);
                    continue;
                }
            }

            found = true;
            playersWithCharm.add(player.getUUID());
            break;
        }

        if (!found) {
            DeathCharm.LOGGER.info("No Death Charm found on player {} [{}].", player.getDisplayName().getString(), player.getStringUUID());
        } else {
            DeathCharm.LOGGER.info("{} [{}] had charm, uses decremented.", player.getDisplayName().getString(), player.getStringUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        UUID uuid = serverPlayer.getUUID();

        if (!playersWithCharm.contains(uuid)) return;
        playersWithCharm.remove(uuid);

        ServerLevel serverLevel = (ServerLevel) serverPlayer.getCommandSenderWorld();
        List<GraveComponent> graves = DeathInfoManager.INSTANCE.getBackupData(new ResolvableProfile(serverPlayer.getGameProfile()));
        if (graves.isEmpty()) return;

        GraveComponent lastUnclaimed = null;
        for (GraveComponent grave : graves) {
            if (grave.getStatus() == GraveStatus.UNCLAIMED) {
                lastUnclaimed = grave;
            }
        }

        if (lastUnclaimed != null) {
            lastUnclaimed.applyToPlayer(serverPlayer, serverLevel, serverPlayer.position(), true);
            lastUnclaimed.setStatus(GraveStatus.CLAIMED);
            lastUnclaimed.removeGraveBlock();

            DeathCharm.LOGGER.info("Restored grave for {} at {}", serverPlayer.getName().getString(), lastUnclaimed.getPos());
        }
    }
}