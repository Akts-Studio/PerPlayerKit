/*
 * Copyright 2022-2025 Noah Ross
 *
 * This file is part of TrKit.
 *
 * TrKit is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * TrKit is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with TrKit. If not, see <https://www.gnu.org/licenses/>.
 */
package dev.noah.perplayerkit.listeners;

import dev.noah.perplayerkit.KitManager;
import dev.noah.perplayerkit.commands.RekitToggleCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class AutoRekitListener implements Listener {

    private final Plugin plugin;

    public AutoRekitListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // Server-wide gate (config)
        if (!plugin.getConfig().getBoolean("feature.rekit-on-respawn", false)) {
            return;
        }

        final Player player = e.getEntity();

        if (!player.hasPermission("perplayerkit.rekitonrespawn")) {
            return;
        }

        // Per-player toggle (default: disabled)
        boolean enabled = player.getPersistentDataContainer()
                .getOrDefault(RekitToggleCommand.REKIT_ON_DEATH_KEY, PersistentDataType.BYTE, (byte) 0) == (byte) 1;
        if (!enabled) {
            return;
        }

        // Folia: Poll for the player to be "alive" and ready in their region
        final java.util.UUID uuid = player.getUniqueId();
        final java.util.concurrent.atomic.AtomicInteger attempts = new java.util.concurrent.atomic.AtomicInteger(0);

        dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runTimer((task) -> {
            int attempt = attempts.incrementAndGet();
            Player p = org.bukkit.Bukkit.getPlayer(uuid);

            // If player is truly back in the game and alive
            if (p != null && p.isOnline() && !p.isDead()) {
                dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runAtEntity(p, (foliaTask) -> {
                    KitManager.get().loadLastKit(p);
                });
                task.cancel();
            } else if (attempt >= 20) {
                // Stop after ~5 seconds if they haven't respawned
                task.cancel();
            }
        }, 5L, 5L);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent e) {
        // Server-wide gate (config)
        if (!plugin.getConfig().getBoolean("feature.rekit-on-kill", false)) {
            return;
        }

        Player killer = e.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        if (!killer.hasPermission("perplayerkit.rekitonkill")) {
            return;
        }

        // Per-player toggle (default: disabled)
        boolean enabled = killer.getPersistentDataContainer()
                .getOrDefault(RekitToggleCommand.REKIT_ON_KILL_KEY, PersistentDataType.BYTE, (byte) 0) == (byte) 1;
        if (!enabled) {
            return;
        }

        long delay = plugin.getConfig().getLong("feature.rekit-on-kill-delay", 0L);
        dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runAtEntityLater(killer, (task) -> {
            KitManager.get().loadLastKit(killer);
        }, delay);
    }
}

