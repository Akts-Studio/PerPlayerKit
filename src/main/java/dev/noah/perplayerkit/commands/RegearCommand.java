package dev.noah.perplayerkit.commands;

import dev.noah.perplayerkit.KitManager;
import dev.noah.perplayerkit.TrKit;
import dev.noah.perplayerkit.listeners.RegearListener;
import dev.noah.perplayerkit.util.BroadcastManager;
import dev.noah.perplayerkit.util.DisabledCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegearCommand implements CommandExecutor {

    private final TrKit plugin;
    private final RegearListener listener;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public RegearCommand(TrKit plugin, RegearListener listener) {
        this.plugin = plugin;
        this.listener = listener;
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

        // Cooldown check
        int cooldownSec = plugin.getConfig().getInt("regear.command-cooldown", 0);
        if (cooldownSec > 0) {
            long lastUse = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUse < cooldownSec * 1000L) {
                player.sendMessage("§cBu komutu kullanmak için beklemelisiniz!");
                return true;
            }
        }

        // Elytra check
        boolean allowElytra = plugin.getConfig().getBoolean("regear.allow-while-using-elytra", true);
        if (!allowElytra && player.isGliding()) {
            player.sendMessage("§cUçarken regear kullanamazsınız!");
            return true;
        }

        // Damage timer check
        int damageTimer = plugin.getConfig().getInt("regear.damage-timer", 0);
        if (damageTimer > 0) {
            long lastDamage = listener.getLastDamage(player.getUniqueId());
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastDamage < damageTimer * 1000L) {
                player.sendMessage("§cSavaş halindeyken regear kullanamazsınız!");
                return true;
            }
        }

        String mode = plugin.getConfig().getString("regear.regear-mode", "command");
        if (label.equalsIgnoreCase("rg")) {
            mode = plugin.getConfig().getString("regear.rg-mode", "command");
        }

        if (mode.equalsIgnoreCase("shulker")) {
            giveRegearShulker(player);
        } else {
            executeRegear(player);
        }

        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        return true;
    }

    private void executeRegear(Player player) {
        int lastKit = KitManager.get().getLastKitLoaded(player.getUniqueId());
        if (lastKit == -1) {
            player.sendMessage("§cHenüz bir kit yüklemediniz!");
            return;
        }

        boolean success = KitManager.get().regearKit(player, lastKit);
        if (success) {
            BroadcastManager.get().broadcastPlayerRegeared(player);
            player.sendMessage("§aBaşarıyla regear yapıldı!");
        } else {
            player.sendMessage("§cRegear başarısız!");
        }
    }

    private void giveRegearShulker(Player player) {
        ItemStack shulker = new ItemStack(Material.CYAN_SHULKER_BOX);
        ItemMeta meta = shulker.getItemMeta();
        if (meta != null) {
            meta.displayName(MiniMessage.miniMessage().deserialize("<gradient:#00ffff:#008080><b>Regear Shulker</b></gradient>"));
            meta.getPersistentDataContainer().set(RegearListener.REGEAR_SHULKER_KEY, PersistentDataType.BYTE, (byte) 1);
            shulker.setItemMeta(meta);
        }
        player.getInventory().addItem(shulker);
        player.sendMessage("§aRegear Shulker kutusu verildi!");
    }
}
