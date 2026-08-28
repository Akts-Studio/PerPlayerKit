package dev.noah.perplayerkit.commands;

import dev.noah.perplayerkit.TrKit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KitBroadcastCommand implements CommandExecutor, TabCompleter {

    private final TrKit plugin;
    public static final NamespacedKey ENABLED_KEY = new NamespacedKey(TrKit.getPlugin(), "broadcast_enabled");
    public static final NamespacedKey DISTANCE_KEY = new NamespacedKey(TrKit.getPlugin(), "broadcast_distance");

    public KitBroadcastCommand(TrKit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        boolean defaultEnabled = plugin.getConfig().getBoolean("broadcast-settings.default-enabled", true);
        int defaultDistance = plugin.getConfig().getInt("broadcast-settings.default-distance", 250);
        List<Integer> allowedDistances = plugin.getConfig().getIntegerList("broadcast-settings.distances");
        if (allowedDistances.isEmpty()) {
            allowedDistances = List.of(100, 150, 200, 250);
        }

        boolean currentEnabled = pdc.getOrDefault(ENABLED_KEY, PersistentDataType.BYTE,
                (byte) (defaultEnabled ? 1 : 0)) == (byte) 1;
        int currentDistance = pdc.getOrDefault(DISTANCE_KEY, PersistentDataType.INTEGER, defaultDistance);

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("toggle"))) {
            // Toggle enabled state
            boolean newEnabled = !currentEnabled;
            pdc.set(ENABLED_KEY, PersistentDataType.BYTE, (byte) (newEnabled ? 1 : 0));

            if (newEnabled) {
                player.sendMessage(ChatColor.GREEN + "Kit yayın mesajları açıldı.");
            } else {
                player.sendMessage(ChatColor.RED + "Kit yayın mesajları kapatıldı.");
            }
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("distance")) {
            if (!currentEnabled) {
                player.sendMessage(ChatColor.RED
                        + "Kit yayın mesajları kapalıyken mesafeyi değiştiremezsiniz. Önce mesajları açın.");
                return true;
            }

            // Cycle through distances
            int currentIndex = allowedDistances.indexOf(currentDistance);
            int nextIndex = (currentIndex + 1) % allowedDistances.size();
            int newDistance = allowedDistances.get(nextIndex);

            pdc.set(DISTANCE_KEY, PersistentDataType.INTEGER, newDistance);
            player.sendMessage(ChatColor.GREEN + "Kit yayın mesafesi " + newDistance + " blok olarak ayarlandı.");
            return true;
        }

        player.sendMessage(ChatColor.RED + "Kullanım: /kitbroadcast [toggle|distance]");
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("toggle");
            completions.add("distance");
            return completions;
        }
        return new ArrayList<>();
    }
}

