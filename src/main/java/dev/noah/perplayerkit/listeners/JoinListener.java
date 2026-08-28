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
import dev.noah.perplayerkit.util.BroadcastManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class JoinListener implements Listener {

    private final Plugin plugin;

    public JoinListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();


        UUID uuid = player.getUniqueId();

        //  KitManager.loadFromSQL(uuid);

        dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runAsync((wrappedTask) -> {
            KitManager.get().loadPlayerDataFromDB(uuid);
        });


        // Check if MOTD is enabled and send MOTD messages
        if (plugin.getConfig().getBoolean("motd.enabled")) {
            List<Component> motdMessages = new ArrayList<>();
            plugin.getConfig().getStringList("motd.message").forEach(message -> motdMessages.add(MiniMessage.miniMessage().deserialize(message)));

            long delayTicks = plugin.getConfig().getLong("motd.delay") * 20L;
            if (delayTicks <= 0) delayTicks = 1;
            dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runAtEntityLater(player, new Runnable() {
                @Override
                public void run() {
                    motdMessages.forEach(message -> BroadcastManager.get().sendComponentMessage(player, message));
                }
            }, delayTicks);
        }
    }


}



