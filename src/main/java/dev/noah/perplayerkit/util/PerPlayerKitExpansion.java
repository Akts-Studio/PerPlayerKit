package dev.noah.perplayerkit.util;

import dev.noah.perplayerkit.TrKit;
import dev.noah.perplayerkit.commands.KitBroadcastCommand;
import dev.noah.perplayerkit.commands.RekitToggleCommand;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PerPlayerKitExpansion extends PlaceholderExpansion {

    private final TrKit plugin;

    public PerPlayerKitExpansion(TrKit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "perplayerkit";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        boolean defaultEnabled = plugin.getConfig().getBoolean("broadcast-settings.default-enabled", true);
        int defaultDistance = plugin.getConfig().getInt("broadcast-settings.default-distance", 250);

        if (identifier.equalsIgnoreCase("broadcast_status")) {
            boolean enabled = pdc.getOrDefault(KitBroadcastCommand.ENABLED_KEY, PersistentDataType.BYTE,
                    (byte) (defaultEnabled ? 1 : 0)) == (byte) 1;
            if (enabled) {
                return plugin.getConfig().getString("broadcast-settings.status-on", "&aAçık").replace("&", "\u00a7");
            } else {
                return plugin.getConfig().getString("broadcast-settings.status-off", "&cKapalı").replace("&", "\u00a7");
            }
        }

        if (identifier.equalsIgnoreCase("broadcast_distance")) {
            int distance = pdc.getOrDefault(KitBroadcastCommand.DISTANCE_KEY, PersistentDataType.INTEGER,
                    defaultDistance);
            return String.valueOf(distance);
        }

        // Per-player rekit-on-death toggle
        if (identifier.equalsIgnoreCase("rekit_on_death")) {
            boolean enabled = pdc.getOrDefault(RekitToggleCommand.REKIT_ON_DEATH_KEY,
                    PersistentDataType.BYTE, (byte) 0) == (byte) 1;
            if (enabled) {
                return plugin.getConfig().getString("broadcast-settings.status-on", "&aAçık").replace("&", "\u00a7");
            } else {
                return plugin.getConfig().getString("broadcast-settings.status-off", "&cKapalı").replace("&", "\u00a7");
            }
        }

        // Per-player rekit-on-kill toggle
        if (identifier.equalsIgnoreCase("rekit_on_kill")) {
            boolean enabled = pdc.getOrDefault(RekitToggleCommand.REKIT_ON_KILL_KEY,
                    PersistentDataType.BYTE, (byte) 0) == (byte) 1;
            if (enabled) {
                return plugin.getConfig().getString("broadcast-settings.status-on", "&aAçık").replace("&", "\u00a7");
            } else {
                return plugin.getConfig().getString("broadcast-settings.status-off", "&cKapalı").replace("&", "\u00a7");
            }
        }

        return null;
    }
}

