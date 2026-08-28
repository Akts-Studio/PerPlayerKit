package dev.noah.perplayerkit.commands;

import dev.noah.perplayerkit.KitManager;
import dev.noah.perplayerkit.util.BroadcastManager;
import dev.noah.perplayerkit.util.DisabledCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RekitCommand implements CommandExecutor {

    private static final String PREFIX = "<green><b>mKit <gray>» <white>";

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

        int lastKit = KitManager.get().getLastKitLoaded(player.getUniqueId());
        if (lastKit == -1) {
            BroadcastManager.get().sendComponentMessage(player,
                    MiniMessage.miniMessage().deserialize("<gray>Henüz bir kit yüklemediniz!"));
            return true;
        }

        boolean loaded = KitManager.get().loadKit(player, lastKit);
        if (!loaded) {
            BroadcastManager.get().sendComponentMessage(player,
                    MiniMessage.miniMessage().deserialize("<gray>Kit yenilenemedi!"));
        }

        return true;
    }
}
