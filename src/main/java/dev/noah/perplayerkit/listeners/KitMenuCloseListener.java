package dev.noah.perplayerkit.listeners;

import dev.noah.perplayerkit.KitManager;
import dev.noah.perplayerkit.gui.GUI;
import dev.noah.perplayerkit.util.StyleManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class KitMenuCloseListener implements Listener {

    @EventHandler
    public void onKitEditorClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if (inv.getSize() == 54) {
            if (inv.getLocation() == null) {
                InventoryView view = e.getView();
                String title = view.getTitle();

                // DÜZELTME 1: Senin GUI'deki başlığın: " " + slot + ". ᴋɪᴛɪ ᴇᴅɪᴛʟᴇ"
                // Bu yüzden ". ᴋɪᴛɪ ᴇᴅɪᴛʟᴇ" içeriyor mu diye bakıyoruz.
                if (title.endsWith(". ᴋɪᴛɪ ᴇᴅɪᴛʟᴇ")) {
                    Player p = (Player) e.getPlayer();
                    UUID uuid = p.getUniqueId();

                    try {
                        // Başlıktan sadece numarayı çekiyoruz.
                        // " 1. ᴋɪᴛɪ ᴇᴅɪᴛʟᴇ" -> ". ᴋɪᴛɪ ᴇᴅɪᴛʟᴇ" kısmını sil -> " 1" -> boşluğu sil -> "1"
                        int slot = Integer.parseInt(title.replace(". ᴋɪᴛɪ ᴇᴅɪᴛʟᴇ", "").trim());

                        ItemStack[] kit = new ItemStack[41];
                        ItemStack[] chestitems = e.getInventory().getContents();

                        for (int i = 0; i < 41; i++) {
                            if (chestitems[i] == null) {
                                kit[i] = null;
                            } else {
                                kit[i] = chestitems[i].clone();
                            }
                        }
                        KitManager.get().savekit(uuid, slot, kit);
                        // p.sendMessage("§aKit " + slot + " kaydedildi."); // Debug için açabilirsin
                    } catch (NumberFormatException ex) {
                        // Sayı çekilemezse hata vermesin diye boş geçiyoruz
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPublicKitEditorClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if (inv.getSize() == 54) {
            if (inv.getLocation() == null) {
                InventoryView view = e.getView();
                String title = view.getTitle();

                // DÜZELTME 2: GUI'de başlık "ʜᴀᴢɪʀ ᴋɪᴛʟᴇʀ: " idi.
                if (title.startsWith("ʜᴀᴢɪʀ ᴋɪᴛʟᴇʀ: ")) {
                    Player player = (Player) e.getPlayer();
                    // Başlıktan ID'yi al
                    String publickit = title.replace("ʜᴀᴢɪʀ ᴋɪᴛʟᴇʀ: ", "").trim();

                    ItemStack[] kit = new ItemStack[41];
                    ItemStack[] chestitems = e.getInventory().getContents();

                    for (int i = 0; i < 41; i++) {
                        if (chestitems[i] == null) {
                            kit[i] = null;
                        } else {
                            kit[i] = chestitems[i].clone();
                        }
                    }
                    KitManager.get().savePublicKit(player, publickit, kit);
                }
            }
        }
    }

    @EventHandler
    public void onEnderchestEditorClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if (inv.getSize() == 54) {
            if (inv.getLocation() == null) {
                InventoryView view = e.getView();
                String title = view.getTitle();

                // DÜZELTME 3: GUI'de başlık "ᴇɴᴅᴇʀ ᴄʜᴇꜱᴛ: "
                if (title.startsWith("ᴇɴᴅᴇʀ ᴄʜᴇꜱᴛ: ")) {
                    Player p = (Player) e.getPlayer();
                    UUID uuid = p.getUniqueId();

                    try {
                        int slot = Integer.parseInt(title.replace("ᴇɴᴅᴇʀ ᴄʜᴇꜱᴛ: ", "").trim());
                        ItemStack[] kit = new ItemStack[27];
                        ItemStack[] chestitems = e.getInventory().getContents();

                        for (int i = 0; i < 27; i++) {
                            if (chestitems[i + 9] == null) {
                                kit[i] = null;
                            } else {
                                kit[i] = chestitems[i + 9].clone();
                            }
                        }
                        KitManager.get().saveEC(uuid, slot, kit);
                    } catch (NumberFormatException ex) {
                        // Hata yok say
                    }
                }
            }
        }
    }

    // Inspect kısımlarını (Admin kontrolü) orijinal bıraktım çünkü GUI'de oraların başlıklarını
    // tam olarak nasıl değiştirdiğini görmedim. Eğer inspect çalışmazsa oraları da Türkçeye uyarlamalısın.
    // Şimdilik sadece kendi kitini kaydetmen için üsttekiler yeterli.

    // ... (InspectListener kodları aynı kalabilir veya silebilirsin, kritik değil)
}