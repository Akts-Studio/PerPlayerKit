package dev.noah.perplayerkit.commands;

import dev.noah.perplayerkit.gui.GUI;
import dev.noah.perplayerkit.util.BroadcastManager;
import dev.noah.perplayerkit.util.DisabledCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class OpenKitRoomCommand implements CommandExecutor {

    private final Plugin plugin;

    public OpenKitRoomCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        if (DisabledCommand.isBlockedInWorld(player)) {
            return true;
        }

        BroadcastManager.get().broadcastPlayerOpenedKitRoom(player);
        GUI gui = new GUI(plugin);
        gui.OpenKitRoom(player);
        return true;
    }
}
