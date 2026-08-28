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
package dev.noah.perplayerkit.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class BroadcastManager {

    private static final int LINE_LENGTH = 60; // Length of the strikethrough line
    private static final String FIGURE_SPACE = "\u2007"; // A whitespace character of consistent width
    private static BroadcastManager instance;
    private final Plugin plugin;
    private final CooldownManager repairBroadcastCooldown = new CooldownManager(5);
    private final CooldownManager kitroomBroadcastCooldown = new CooldownManager(15);
    private final BukkitAudiences audience;
    private Component prefix;
    private com.tcoded.folialib.wrapper.task.WrappedTask scheduledBroadcastTask;

    public BroadcastManager(Plugin plugin) {
        this.plugin = plugin;
        audience = BukkitAudiences.create(plugin);
        // Prefix değiştirildi
        prefix = MiniMessage.miniMessage()
                .deserialize(plugin.getConfig().getString("prefix", "<green><b>mKit <gray>» <white>"));
        instance = this;
    }

    public static BroadcastManager get() {
        if (instance == null) {
            throw new IllegalStateException("BroadcastManager has not been initialized yet!");
        }
        return instance;
    }

    public static Component generateBroadcastComponent(String message) {
        String strikeThroughLine = "<gray>" + " ".repeat(3) + "<st>" + FIGURE_SPACE.repeat(LINE_LENGTH) + "</st>";

        int messageLength = MiniMessage.miniMessage().stripTags(message).length();

        int padding = (LINE_LENGTH - messageLength) / 2;

        String formattedMessage = strikeThroughLine + "\n\n" + " ".repeat(3) + FIGURE_SPACE.repeat(Math.max(padding, 0))
                + message + "\n\n" + strikeThroughLine;

        return MiniMessage.miniMessage().deserialize(formattedMessage);
    }

    private boolean isKitLoadingMessage(MessageKey key) {
        return key == MessageKey.PLAYER_LOADED_PRIVATE_KIT ||
                key == MessageKey.PLAYER_LOADED_PUBLIC_KIT ||
                key == MessageKey.PLAYER_LOADED_ENDER_CHEST;
    }

    private void broadcastMessage(Player player, String message, String permission) {
        World world = player.getWorld();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        boolean defaultEnabled = plugin.getConfig().getBoolean("broadcast-settings.default-enabled", true);
        int defaultDistance = plugin.getConfig().getInt("broadcast-settings.default-distance", 250);

        for (Player broadcastPlayer : world.getPlayers()) {
            org.bukkit.persistence.PersistentDataContainer pdc = broadcastPlayer.getPersistentDataContainer();
            boolean enabled = pdc.getOrDefault(dev.noah.perplayerkit.commands.KitBroadcastCommand.ENABLED_KEY,
                    org.bukkit.persistence.PersistentDataType.BYTE, (byte) (defaultEnabled ? 1 : 0)) == (byte) 1;
            int distanceSetting = pdc.getOrDefault(dev.noah.perplayerkit.commands.KitBroadcastCommand.DISTANCE_KEY,
                    org.bukkit.persistence.PersistentDataType.INTEGER, defaultDistance);

            if (!enabled)
                continue;

            if (broadcastPlayer.getLocation().distance(player.getLocation()) < distanceSetting) {
                // Check if the broadcast player has permission to see this message
                if (broadcastPlayer.hasPermission(permission)) {
                    audience.player(broadcastPlayer)
                            .sendMessage(prefix.append(MiniMessage.miniMessage().deserialize(message)));
                }
            }
        }
    }

    private void broadcastMessage(Player player, BroadcastManager.MessageKey key, CooldownManager cooldownManager) {

        if (!plugin.getConfig().getBoolean("feature.broadcast-on-player-action", true)) {
            return;
        }

        if (plugin.getConfig().getBoolean("messages.disable-kit-messages", false)) {
            return;
        }

        // Check if this is a kit loading message and if kit message broadcasts are
        // disabled
        if (isKitLoadingMessage(key) && !plugin.getConfig().getBoolean("feature.broadcast-kit-messages", true)) {
            return;
        }

        if (cooldownManager != null && cooldownManager.isOnCooldown(player)) {
            return;
        }

        String messageConfigPath = key.getKey();
        String enabledPath = messageConfigPath + ".enabled";
        String messagePath = messageConfigPath + ".message";
        String permissionPath = messageConfigPath + ".permission";

        // Check if message is enabled
        if (!plugin.getConfig().getBoolean(enabledPath, true)) {
            return;
        }

        // Varsayılan mesaj Türkçeleştirildi ve Aqua yerine Green kullanıldı
        String message = plugin.getConfig().getString(messagePath, "<gray><green>%player%</green> " +
                "bir işlem gerçekleştirdi.</gray>");
        String permission = plugin.getConfig().getString(permissionPath, "perplayerkit.kitnotify");

        String playerName;
        if (plugin.getConfig().getBoolean("use-display-name", false)) {
            playerName = player.getDisplayName();
        } else {
            playerName = player.getName();
        }

        message = message.replace("%player%", playerName);

        broadcastMessage(player, message, permission);

        if (cooldownManager != null) {
            cooldownManager.setCooldown(player);
        }
    }

    public void broadcastPlayerRepaired(Player player) {
        broadcastMessage(player, MessageKey.PLAYER_REPAIRED, repairBroadcastCooldown);
    }

    public void broadcastPlayerHealed(Player player) {
        broadcastMessage(player, MessageKey.PLAYER_HEALED, null);
    }

    public void broadcastPlayerOpenedKitRoom(Player player) {
        broadcastMessage(player, MessageKey.PLAYER_OPENED_KIT_ROOM, kitroomBroadcastCooldown);
    }

    public void broadcastPlayerLoadedPrivateKit(Player player) {
        broadcastMessage(player, MessageKey.PLAYER_LOADED_PRIVATE_KIT, null);
    }

    public void broadcastPlayerLoadedPublicKit(Player player) {
        broadcastMessage(player, MessageKey.PLAYER_LOADED_PUBLIC_KIT, null);
    }

    public void broadcastPlayerLoadedEnderChest(Player player) {
        broadcastMessage(player, MessageKey.PLAYER_LOADED_ENDER_CHEST, null);
    }

    public void broadcastPlayerCopiedKit(Player player) {
        broadcastMessage(player, MessageKey.PLAYER_COPIED_KIT, null);
    }

    public void broadcastPlayerCopiedEC(Player player) {
        broadcastMessage(player, MessageKey.PLAYER_COPIED_EC, null);
    }

    public void broadcastPlayerRegeared(Player player) {
        broadcastMessage(player, MessageKey.PLAYER_REGEARED, null);
    }

    public void startScheduledBroadcast() {
        stopScheduledBroadcast(); // Stop existing if any

        List<Component> messages = new ArrayList<>();
        plugin.getConfig().getStringList("scheduled-broadcast.messages")
                .forEach(message -> messages.add(generateBroadcastComponent(message)));

        if (messages.isEmpty())
            return;

        int[] index = { 0 };

        if (plugin.getConfig().getBoolean("scheduled-broadcast.enabled")) {
            scheduledBroadcastTask = dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runTimerAsync(new Runnable() {
                @Override
                public void run() {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        audience.player(player).sendMessage(messages.get(index[0]));
                    }
                    index[0] = (index[0] + 1) % messages.size();
                }
            }, 0, plugin.getConfig().getInt("scheduled-broadcast.period") * 20L);
        }
    }

    public void stopScheduledBroadcast() {
        if (scheduledBroadcastTask != null) {
            scheduledBroadcastTask.cancel();
            scheduledBroadcastTask = null;
        }
    }

    public void reloadPrefix() {
        // Refresh prefix from config
        prefix = MiniMessage.miniMessage()
                .deserialize(plugin.getConfig().getString("prefix", "<green><b>mKit <gray>» <white>"));
    }

    public void sendComponentMessage(Player player, Component message) {
        audience.player(player).sendMessage(message);
    }

    public enum MessageKey {
        PLAYER_REPAIRED("messages.player-repaired"), PLAYER_HEALED("messages.player-healed"),
        PLAYER_OPENED_KIT_ROOM("messages.player-opened-kit-room"),
        PLAYER_LOADED_PRIVATE_KIT("messages.player-loaded-private-kit"),
        PLAYER_LOADED_PUBLIC_KIT("messages.player-loaded-public-kit"),
        PLAYER_LOADED_ENDER_CHEST("messages.player-loaded-enderchest"), PLAYER_COPIED_KIT("messages.player-copied-kit"),
        PLAYER_COPIED_EC("messages.player-copied-ec"), PLAYER_REGEARED("messages.player-regeared");

        private final String key;

        MessageKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
