package dev.noah.perplayerkit.listeners;

import dev.noah.perplayerkit.KitManager;
import dev.noah.perplayerkit.TrKit;
import dev.noah.perplayerkit.util.BroadcastManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegearListener implements Listener {

    public static final NamespacedKey REGEAR_SHULKER_KEY = new NamespacedKey(TrKit.getPlugin(), "regear_shulker");
    private final Map<UUID, Long> lastDamageTime = new HashMap<>();
    private final TrKit plugin;

    public RegearListener(TrKit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            lastDamageTime.put(player.getUniqueId(), System.currentTimeMillis());
        }
        if (event.getDamager() instanceof Player player) {
            lastDamageTime.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    public long getLastDamage(UUID uuid) {
        return lastDamageTime.getOrDefault(uuid, 0L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(REGEAR_SHULKER_KEY, PersistentDataType.BYTE)) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            // Combat check
            int damageTimer = plugin.getConfig().getInt("regear.damage-timer", 0);
            if (damageTimer > 0) {
                long lastDamage = getLastDamage(player.getUniqueId());
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastDamage < damageTimer * 1000L) {
                    player.sendMessage("§cSavaş halindeyken regear kullanamazsınız!");
                    return;
                }
            }

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
    }
}
