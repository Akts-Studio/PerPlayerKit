package dev.noah.perplayerkit.commands;

import dev.noah.perplayerkit.TrKit;
import dev.noah.perplayerkit.util.BroadcastManager;
import dev.noah.perplayerkit.util.SoundManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

import java.util.List;

public class RekitToggleCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    public static final NamespacedKey REKIT_ON_DEATH_KEY = new NamespacedKey(TrKit.getPlugin(),
            "rekit_on_death");
    public static final NamespacedKey REKIT_ON_KILL_KEY = new NamespacedKey(TrKit.getPlugin(), "rekit_on_kill");
    public static final NamespacedKey LAST_KIT_KEY = new NamespacedKey(TrKit.getPlugin(), "last_kit_used");

    private static void msg(Player player, String miniMsg) {
        BroadcastManager.get().sendComponentMessage(player, MM.deserialize(miniMsg));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        if (args.length == 0) {
            msg(player, "<gray>Kullanım: /rekittoggle <death|kill>");
            return true;
        }

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        switch (args[0].toLowerCase()) {
            case "death" -> {
                if (!player.hasPermission("perplayerkit.rekitonrespawn")) {
                    SoundManager.playFailure(player);
                    msg(player, "<red>Bu özellik için yetkin yok!");
                    return true;
                }
                boolean current = pdc.getOrDefault(REKIT_ON_DEATH_KEY, PersistentDataType.BYTE, (byte) 0) == (byte) 1;
                boolean next = !current;
                pdc.set(REKIT_ON_DEATH_KEY, PersistentDataType.BYTE, (byte) (next ? 1 : 0));
                if (next) {
                    SoundManager.playSuccess(player);
                    msg(player, "<gray>Öldüğünde Rekit: <green>Açık</green>");
                } else {
                    SoundManager.playClick(player);
                    msg(player, "<gray>Öldüğünde Rekit: <red>Kapalı</red>");
                }
            }
            case "kill" -> {
                if (!player.hasPermission("perplayerkit.rekitonkill")) {
                    SoundManager.playFailure(player);
                    msg(player, "<red>Bu özellik için yetkin yok!");
                    return true;
                }
                boolean current = pdc.getOrDefault(REKIT_ON_KILL_KEY, PersistentDataType.BYTE, (byte) 0) == (byte) 1;
                boolean next = !current;
                pdc.set(REKIT_ON_KILL_KEY, PersistentDataType.BYTE, (byte) (next ? 1 : 0));
                if (next) {
                    SoundManager.playSuccess(player);
                    msg(player, "<gray>Öldürdüğünde Rekit: <green>Açık</green>");
                } else {
                    SoundManager.playClick(player);
                    msg(player, "<gray>Öldürdüğünde Rekit: <red>Kapalı</red>");
                }
            }
            default -> msg(player, "<gray>Kullanım: /rekittoggle <death|kill>");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("death", "kill");
        }
        return List.of();
    }
}

