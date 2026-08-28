package dev.noah.perplayerkit.util;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    private static MessageManager instance;
    private final Plugin plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private final BukkitAudiences audience;
    private Component prefix;

    public MessageManager(Plugin plugin) {
        this.plugin = plugin;
        this.audience = BukkitAudiences.create(plugin);
        reloadMessages();
        instance = this;
    }

    public static MessageManager get() {
        if (instance == null) {
            throw new IllegalStateException("MessageManager has not been initialized!");
        }
        return instance;
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defConfigStream = plugin.getResource("messages.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            messagesConfig.setDefaults(defConfig);
        }
        
        String prefixStr = messagesConfig.getString("prefix", "<green><b>trkit <gray>» <white>");
        this.prefix = MiniMessage.miniMessage().deserialize(prefixStr);
    }

    public void sendMessage(Player player, String key, String... placeholders) {
        String msg = messagesConfig.getString(key);
        if (msg == null || msg.isEmpty()) {
            msg = "<red>Message key not found: " + key + "</red>";
        }

        if (placeholders != null && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                msg = msg.replace(placeholders[i], placeholders[i + 1]);
            }
        }

        Component component = MiniMessage.miniMessage().deserialize(msg);
        audience.player(player).sendMessage(prefix.append(component));
    }

    public Component getComponent(String key, String... placeholders) {
        String msg = messagesConfig.getString(key);
        if (msg == null || msg.isEmpty()) {
            msg = "<red>Message key not found: " + key + "</red>";
        }

        if (placeholders != null && placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                msg = msg.replace(placeholders[i], placeholders[i + 1]);
            }
        }

        return MiniMessage.miniMessage().deserialize(msg);
    }
}
