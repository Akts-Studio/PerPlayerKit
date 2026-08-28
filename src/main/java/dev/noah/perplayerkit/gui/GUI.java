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
package dev.noah.perplayerkit.gui;

import dev.noah.perplayerkit.ItemFilter;
import dev.noah.perplayerkit.KitManager;
import dev.noah.perplayerkit.KitRoomDataManager;
import dev.noah.perplayerkit.PublicKit;
import dev.noah.perplayerkit.util.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static dev.noah.perplayerkit.gui.ItemUtil.addHideFlags;
import static dev.noah.perplayerkit.gui.ItemUtil.createItem;
import static dev.noah.perplayerkit.gui.ItemUtil.createGlassPane;

public class GUI {
    private final Plugin plugin;
    private final boolean filterItemsOnImport;
    private static final Set<UUID> kitDeletionFlag = new HashSet<>();

    public GUI(Plugin plugin) {
        this.plugin = plugin;
        this.filterItemsOnImport = plugin.getConfig().getBoolean("anti-exploit.import-filter", false);
    }

    public static void addLoadPublicKit(Slot slot, String id) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            KitManager.get().loadPublicKit(player, id);
            info.getClickedMenu().close();
        });
    }

    public static Menu createPublicKitMenu() {
        return ChestMenu.builder(6).title("ʜᴀᴢɪʀ ᴋɪᴛʟᴇʀ:").redraw(true).build();
    }

    public static boolean removeKitDeletionFlag(Player player) {
        return kitDeletionFlag.remove(player.getUniqueId());
    }

    public void OpenKitMenu(Player p, int slot) {
        Menu menu = createKitMenu(slot);

        if (KitManager.get().getItemStackArrayById(p.getUniqueId().toString() + slot) != null) {
            ItemStack[] kit = KitManager.get().getItemStackArrayById(p.getUniqueId().toString() + slot);
            for (int i = 0; i < 41; i++) {
                menu.getSlot(i).setItem(kit[i]);
            }
        }
        for (int i = 0; i < 41; i++) {
            allowModification(menu.getSlot(i));
        }
        for (int i = 41; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());
        }
        menu.getSlot(45).setItem(createItem(Material.CHAINMAIL_BOOTS, 1, "<gray><b>ʙᴏᴛ</b></gray>"));
        menu.getSlot(46).setItem(createItem(Material.CHAINMAIL_LEGGINGS, 1, "<gray><b>ᴘᴀɴᴛᴀʟᴏɴ</b></gray>"));
        menu.getSlot(47).setItem(createItem(Material.CHAINMAIL_CHESTPLATE, 1, "<gray><b>ɢöğüꜱʟüᴋ</b></gray>"));
        menu.getSlot(48).setItem(createItem(Material.CHAINMAIL_HELMET, 1, "<gray><b>ᴋᴀꜱᴋ</b></gray>"));
        menu.getSlot(49).setItem(createItem(Material.SHIELD, 1, "<gray><b>ᴅɪğᴇʀ ᴇʟ</b></gray>"));

        menu.getSlot(51).setItem(
                createItem(Material.CHEST, 1, "<green><b>ɪçᴇ ᴀᴋᴛᴀʀ</b></green>", "<gray>● Envanterinden aktar</gray>"));
        menu.getSlot(52).setItem(
                createItem(Material.BARRIER, 1, "<red><b>ᴋɪᴛɪ ʙᴏşᴀʟᴛ</b></red>", "<gray>● Shift+Sol tık</gray>"));
        menu.getSlot(53).setItem(createItem(Material.OAK_DOOR, 1, "<red><b>ɢᴇʀɪ</b></red>"));
        addMainButton(menu.getSlot(53));
        addClear(menu.getSlot(52));
        addImport(menu.getSlot(51));
        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);

        menu.open(p);
    }

    public void OpenPublicKitEditor(Player p, String kitId) {
        Menu menu = createPublicKitMenu(kitId);

        if (KitManager.get().getItemStackArrayById(IDUtil.getPublicKitId(kitId)) != null) {
            ItemStack[] kit = KitManager.get().getItemStackArrayById(IDUtil.getPublicKitId(kitId));
            for (int i = 0; i < 41; i++) {
                menu.getSlot(i).setItem(kit[i]);
            }
        }
        for (int i = 0; i < 41; i++) {
            allowModification(menu.getSlot(i));
        }
        for (int i = 41; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());
        }
        menu.getSlot(45).setItem(createItem(Material.CHAINMAIL_BOOTS, 1, "<gray><b>ʙᴏᴛ</b></gray>"));
        menu.getSlot(46).setItem(createItem(Material.CHAINMAIL_LEGGINGS, 1, "<gray><b>ᴘᴀɴᴛᴀʟᴏɴ</b></gray>"));
        menu.getSlot(47).setItem(createItem(Material.CHAINMAIL_CHESTPLATE, 1, "<gray><b>ɢöğüꜱʟüᴋ</b></gray>"));
        menu.getSlot(48).setItem(createItem(Material.CHAINMAIL_HELMET, 1, "<gray><b>ᴋᴀꜱᴋ</b></gray>"));
        menu.getSlot(49).setItem(createItem(Material.SHIELD, 1, "<gray><b>ᴅɪğᴇʀ ᴇʟ</b></gray>"));

        menu.getSlot(51).setItem(
                createItem(Material.CHEST, 1, "<green><b>ɪçᴇ ᴀᴋᴛᴀʀ</b></green>", "<gray>● Envanterinden aktar</gray>"));
        menu.getSlot(52).setItem(
                createItem(Material.BARRIER, 1, "<red><b>ᴋɪᴛɪ ʙᴏşᴀʟᴛ</b></red>", "<gray>● Shift+Sol tık</gray>"));
        menu.getSlot(53).setItem(createItem(Material.OAK_DOOR, 1, "<red><b>ɢᴇʀɪ</b></red>"));
        addMainButton(menu.getSlot(53));
        addClear(menu.getSlot(52));
        addImport(menu.getSlot(51));
        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);

        menu.open(p);
    }

    public void OpenECKitKenu(Player p, int slot) {
        Menu menu = createECMenu(slot);

        for (int i = 0; i < 9; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());

        }
        for (int i = 36; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());

        }
        if (KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + slot) != null) {

            ItemStack[] kit = KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + slot);
            for (int i = 9; i < 36; i++) {
                menu.getSlot(i).setItem(kit[i - 9]);
            }
        }
        for (int i = 9; i < 36; i++) {
            allowModification(menu.getSlot(i));
        }
        menu.getSlot(51).setItem(createItem(Material.ENDER_CHEST, 1, "<green><b>ɪçᴇ ᴀᴋᴛᴀʀ</b></green>",
                "<gray>● Envanterinden aktar</gray>"));
        menu.getSlot(52).setItem(
                createItem(Material.BARRIER, 1, "<red><b>ᴋɪᴛɪ ʙᴏşᴀʟᴛ</b></red>", "<gray>● Shift+Sol tık</gray>"));
        menu.getSlot(53).setItem(createItem(Material.OAK_DOOR, 1, "<red><b>ɢᴇʀɪ</b></red>"));
        addMainButton(menu.getSlot(53));
        addClear(menu.getSlot(52), 9, 36);
        addImportEC(menu.getSlot(51));
        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
    }

    public void InspectKit(Player p, UUID target, int slot) {
        String playerName = getPlayerName(target);
        Menu menu = createInspectMenu(slot, playerName);

        if (KitManager.get().hasKit(target, slot)) {
            ItemStack[] kit = KitManager.get().getItemStackArrayById(target.toString() + slot);
            for (int i = 0; i < 41; i++) {
                menu.getSlot(i).setItem(kit[i]);
            }
        }
        for (int i = 41; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());
        }
        menu.getSlot(45).setItem(createItem(Material.CHAINMAIL_BOOTS, 1, "<gray><b>ʙᴏᴛ</b></gray>"));
        menu.getSlot(46).setItem(createItem(Material.CHAINMAIL_LEGGINGS, 1, "<gray><b>ᴘᴀɴᴛᴀʟᴏɴ</b></gray>"));
        menu.getSlot(47).setItem(createItem(Material.CHAINMAIL_CHESTPLATE, 1, "<gray><b>ɢöğüꜱʟüᴋ</b></gray>"));
        menu.getSlot(48).setItem(createItem(Material.CHAINMAIL_HELMET, 1, "<gray><b>ᴋᴀꜱᴋ</b></gray>"));
        menu.getSlot(49).setItem(createItem(Material.SHIELD, 1, "<gray><b>ᴅɪğᴇʀ ᴇʟ</b></gray>"));

        menu.getSlot(53).setItem(createItem(Material.OAK_DOOR, 1, "<red><b>ᴍᴇɴüʏü ᴋᴀᴘᴀᴛ</b></red>"));
        menu.getSlot(53).setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            info.getClickedMenu().close();
            SoundManager.playCloseGui(player);
        });

        if (p.hasPermission("perplayerkit.admin")) {
            for (int i = 0; i < 41; i++) {
                allowModification(menu.getSlot(i));
            }
            menu.getSlot(52).setItem(
                    createItem(Material.BARRIER, 1, "<red><b>ᴋɪᴛɪ ʙᴏşᴀʟᴛ</b></red>", "<gray>● Shift+Sol tık</gray>"));
            addClearKit(menu.getSlot(52), target, slot);
        }

        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
        SoundManager.playOpenGui(p);
    }

    public void InspectEc(Player p, UUID target, int slot) {
        String playerName = getPlayerName(target);
        Menu menu = createInspectEcMenu(slot, playerName);

        for (int i = 0; i < 9; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());

        }
        for (int i = 36; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());

        }
        if (KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + slot) != null) {

            ItemStack[] kit = KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + slot);
            for (int i = 9; i < 36; i++) {
                menu.getSlot(i).setItem(kit[i - 9]);
            }
        }

        menu.getSlot(53).setItem(createItem(Material.OAK_DOOR, 1, "<red><b>ᴍᴇɴüʏü ᴋᴀᴘᴀᴛ</b></red>"));
        menu.getSlot(53).setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            info.getClickedMenu().close();
            SoundManager.playCloseGui(player);
        });

        if (p.hasPermission("perplayerkit.admin")) {
            for (int i = 9; i < 36; i++) {
                allowModification(menu.getSlot(i));
            }
            menu.getSlot(52).setItem(createItem(Material.BARRIER, 1, "<red><b>ᴇɴᴅᴇʀᴄʜᴇꜱᴛɪ ʙᴏşᴀʟᴛ</b></red>",
                    "<gray>● Shift+Sol tık</gray>"));
            addClearEnderchest(menu.getSlot(52), target, slot);
        }

        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
        SoundManager.playOpenGui(p);
    }

    public void OpenMainMenu(Player p) {
        Menu menu = createMainMenu(p);

        // 1. Arka planı Cam ile doldur
        for (int i = 0; i < 54; i++) {
            menu.getSlot(i).setItem(createGlassPane());
        }

        // --- 2. SATIR: KİTLER (1-9) ---
        // Slot 10'dan 18'e kadar (k1-k9)
        for (int i = 9; i < 18; i++) {
            int kitId = i - 8; // Slot 10 = Kit 1
            boolean hasVip = p.hasPermission("perplayerkit.kit.vip");
            boolean needsVip = kitId >= 8; // 8-9 VIP

            if (needsVip && !hasVip) {
                menu.getSlot(i).setItem(createItem(
                        Material.END_CRYSTAL, 1, "<#ff6ecc><b>" + kitId + ". ᴋɪᴛ</b></#ff6ecc>",
                        "<gray>● Yüklemek için Sol Tık</gray>",
                        "<gray>● Düzenlemek için Sağ Tık</gray>",
                        "<red>⚠ Bu kit için <b>VIP</b> gereklidir!</red>"));
                menu.getSlot(i).setClickHandler((player, info) -> {
                    SoundManager.playFailure(player);
                    BroadcastManager.get().sendComponentMessage(player,
                            net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                                    "<green><b>mKit <gray>» <white><red>Bu kit için VIP almanız gereklidir!"));
                });
            } else {
                menu.getSlot(i).setItem(createItem(
                        Material.END_CRYSTAL, 1, "<#ff6ecc><b>" + kitId + ". ᴋɪᴛ</b></#ff6ecc>",
                        "<gray>● Yüklemek için Sol Tık</gray>", "<gray>● Düzenlemek için Sağ Tık</gray>"));
                addEditLoad(menu.getSlot(i), kitId);
            }
        }

        // --- 3. SATIR: ENDERCHESTLER (1-9) ---
        // Slot 19'dan 27'ye kadar (ec1-ec9)
        for (int i = 18; i < 27; i++) {
            int ecId = i - 17; // Slot 19 = EC 1
            boolean hasVip = p.hasPermission("perplayerkit.ec.vip");
            boolean needsVip = ecId >= 8; // 8-9 VIP

            if (needsVip && !hasVip) {
                menu.getSlot(i).setItem(createItem(
                        Material.ENDER_CHEST, 1, "<#608a8f><b>" + ecId + ". ᴇɴᴅᴇʀᴄʜᴇꜱᴛ</b></#608a8f>",
                        "<gray>● Oluşturmak/Düzenlemek için Tıkla</gray>",
                        "<red>⚠ Bu EC için <b>VIP</b> gereklidir!</red>"));
                menu.getSlot(i).setClickHandler((player, info) -> {
                    SoundManager.playFailure(player);
                    BroadcastManager.get().sendComponentMessage(player,
                            net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                                    "<green><b>mKit <gray>» <white><red>Bu EC için VIP almanız gereklidir!"));
                });
            } else if (KitManager.get().getItemStackArrayById(p.getUniqueId() + "ec" + ecId) != null) {
                menu.getSlot(i).setItem(createItem(
                        Material.ENDER_CHEST, 1, "<#608a8f><b>" + ecId + ". ᴇɴᴅᴇʀᴄʜᴇꜱᴛ</b></#608a8f>",
                        "<gray>● Yüklemek için Sol Tık</gray>", "<gray>● Düzenlemek için Sağ Tık</gray>"));
                addEditLoadEC(menu.getSlot(i), ecId);
            } else {
                menu.getSlot(i).setItem(createItem(
                        Material.ENDER_CHEST, 1, "<#608a8f><b>" + ecId + ". ᴇɴᴅᴇʀᴄʜᴇꜱᴛ</b></#608a8f>",
                        "<gray>● Oluşturmak için Tıkla</gray>"));
                addEditEC(menu.getSlot(i), ecId);
            }
        }

        // --- 4. SATIR: DURUM / OLUŞTURMA BUTONLARI ---
        // Eski "Kit Exists/Not Found" kısmı buraya geldi.
        // Slot 28'den 34'e kadar (Ortalı)

        // --- 6. SATIR: FONKSİYONEL BUTONLAR ---
        // En alt satıra diziyoruz

        // Kit Odası (Slot 46)
        menu.getSlot(37).setItem(createItem(Material.CREEPER_BANNER_PATTERN, 1, "<#fff8e6><b>ᴋɪᴛ ᴏᴅᴀꜱɪ</b></#fff8e6>"));

        // Hazır Kitler (Slot 48)
        menu.getSlot(39).setItem(createItem(Material.HEART_OF_THE_SEA, 1, "<#4a99c7><b>ʜᴀᴢɪʀ ᴋɪᴛʟᴇʀ</b></#4a99c7>"));

        // Envanter Temizle (Slot 50)
        menu.getSlot(41).setItem(
                createItem(Material.RED_DYE, 1, "<red><b>ᴇɴᴠᴀɴᴛᴇʀɪ ᴛᴇᴍɪᴢʟᴇ</b></red>", "<gray>● Shift+Sağ Tık</gray>"));

        // Tamir Et (Slot 52)
        menu.getSlot(43).setItem(createItem(Material.EXPERIENCE_BOTTLE, 1, "<green><b>ᴇşʏᴀʟᴀʀɪ ᴛᴀᴍɪʀ ᴇᴛ</b></green>"));

        // Menüyü Aç
        addRepairButton(menu.getSlot(43));
        addKitRoom(menu.getSlot(37));
        addPublicKitMenu(menu.getSlot(39));
        addClearButton(menu.getSlot(41));

        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
    }

    public void OpenKitRoom(Player p) {
        OpenKitRoom(p, 0);
    }

    public void OpenKitRoom(Player p, int page) {
        Menu menu = createKitRoom();
        for (int i = 0; i < 45; i++) {
            allowModification(menu.getSlot(i));
        }
        for (int i = 45; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());
        }
        if (KitRoomDataManager.get().getKitRoomPage(page) != null) {
            for (int i = 0; i < 45; i++) {
                menu.getSlot(i).setItem(KitRoomDataManager.get().getKitRoomPage(page)[i]);
            }
        }

        menu.getSlot(45).setItem(createItem(Material.BEACON, 1, "<#92d2f7><b>ᴛᴇᴋʀᴀʀ ᴅᴏʟᴅᴜʀ</b></#92d2f7>"));
        addKitRoom(menu.getSlot(45), page);

        if (!p.hasPermission("perplayerkit.editkitroom")) {
            menu.getSlot(53).setItem(createItem(Material.ARROW, 1, "<red><b>ɢᴇʀɪ</b></red>"));
            addMainButton(menu.getSlot(53));
        } else {
            menu.getSlot(53).setItem(createItem(Material.BARRIER, page + 1, "<red><b>ᴍᴇɴüʏü ᴇᴅɪᴛʟᴇ</b></red>",
                    "<red>ꜱʜɪꜰᴛ+ꜱᴀğ ᴛɪᴋ ɪʟᴇ ᴋᴀʏᴅᴇᴛ</red>"));
            addKitRoomSaveButton(menu.getSlot(53), page);
        }
        addKitRoom(menu.getSlot(47), 0);
        addKitRoom(menu.getSlot(48), 1);
        addKitRoom(menu.getSlot(50), 3);
        addKitRoom(menu.getSlot(51), 4);

        for (int i = 1; i < 6; i++) {
            if (i == 3) {
                continue;
            }
            menu.getSlot(46 + i)
                    .setItem(addHideFlags(createItem(
                            Material.valueOf(plugin.getConfig().getString("kitroom.items." + i + ".material")),
                            "<reset>" + plugin.getConfig().getString("kitroom.items." + i + ".name"))));

        }

        menu.getSlot(page + 47).setItem(ItemUtil.addEnchantLook(menu.getSlot(page + 47).getItem(p)));

        menu.setCursorDropHandler(Menu.ALLOW_CURSOR_DROPPING);
        menu.open(p);
    }

    public Menu ViewPublicKitMenu(Player p, String id) {
        ItemStack[] kit = KitManager.get().getPublicKit(id);

        if (kit == null) {
            dev.noah.perplayerkit.util.MessageManager.get().sendMessage(p, "public-kit-not-found");
            if (p.hasPermission("perplayerkit.admin")) {
                dev.noah.perplayerkit.util.MessageManager.get().sendMessage(p, "public-kit-load-info");
            }
            return null;
        }
        Menu menu = ChestMenu.builder(6).title(StyleManager.get().getPrimaryColor() + "ʜᴀᴢɪʀ ᴋɪᴛ " + id).redraw(true)
                .build();

        for (int i = 0; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());
        }

        for (int i = 9; i < 36; i++) {
            menu.getSlot(i).setItem(kit[i]);
        }
        for (int i = 0; i < 9; i++) {
            menu.getSlot(i + 36).setItem(kit[i]);
        }
        for (int i = 36; i < 41; i++) {
            menu.getSlot(i + 9).setItem(kit[i]);
        }

        menu.getSlot(45).setItem(createItem(Material.CHAINMAIL_BOOTS, 1, "<gray><b>ʙᴏᴛ</b></gray>"));
        menu.getSlot(46).setItem(createItem(Material.CHAINMAIL_LEGGINGS, 1, "<gray><b>ᴘᴀɴᴛᴀʟᴏɴ</b></gray>"));
        menu.getSlot(47).setItem(createItem(Material.CHAINMAIL_CHESTPLATE, 1, "<gray><b>ɢöğüꜱʟüᴋ</b></gray>"));
        menu.getSlot(48).setItem(createItem(Material.CHAINMAIL_HELMET, 1, "<gray><b>ᴋᴀꜱᴋ</b></gray>"));
        menu.getSlot(49).setItem(createItem(Material.SHIELD, 1, "<gray><b>ᴅɪğᴇʀ ᴇʟ</b></gray>"));

        menu.getSlot(52).setItem(createItem(Material.APPLE, 1, "<green><b>ᴋɪᴛɪ ʏüᴋʟᴇ</b></green>"));
        menu.getSlot(53).setItem(createItem(Material.OAK_DOOR, 1, "<red><b>ɢᴇʀɪ</b></red>"));
        addPublicKitMenu(menu.getSlot(53));
        addLoadPublicKit(menu.getSlot(52), id);

        menu.open(p);

        return menu;
    }

    public void OpenPublicKitMenu(Player player) {
        Menu menu = createPublicKitMenu();
        for (int i = 0; i < 54; i++) {
            menu.getSlot(i).setItem(ItemUtil.createGlassPane());
        }

        for (int i = 18; i < 36; i++) {
            menu.getSlot(i).setItem(ItemUtil.createItem(Material.BOOK, 1, "<gray><b>MORE KITS COMING SOON</b></gray>"));
        }

        List<PublicKit> publicKitList = KitManager.get().getPublicKitList();

        for (int i = 0; i < publicKitList.size(); i++) {
            if (KitManager.get().hasPublicKit(publicKitList.get(i).id)) {
                // --- DÜZELTME BURADA ---
                // ChatColor.RESET yerine "<reset>" kullandık.
                if (player.hasPermission("perplayerkit.admin")) {
                    menu.getSlot(i + 18).setItem(createItem(publicKitList.get(i).icon, 1,
                            "<reset>" + publicKitList.get(i).name, "<gray>● [ADMIN] Shift click to edit</gray>"));
                } else {
                    menu.getSlot(i + 18)
                            .setItem(createItem(publicKitList.get(i).icon, 1, "<reset>" + publicKitList.get(i).name));
                }
                addPublicKitButton(menu.getSlot(i + 18), publicKitList.get(i).id);
            } else {
                // --- BURADA DA DÜZELTME YAPILDI ---
                if (player.hasPermission("perplayerkit.admin")) {
                    menu.getSlot(i + 18)
                            .setItem(createItem(publicKitList.get(i).icon, 1,
                                    "<reset>" + publicKitList.get(i).name + " <red><b>[UNASSIGNED]</b></red>",
                                    "<gray>● Admins have not yet setup this kit yet</gray>",
                                    "<gray>● [ADMIN] Shift click to edit</gray>"));
                } else {
                    menu.getSlot(i + 18)
                            .setItem(createItem(publicKitList.get(i).icon, 1,
                                    "<reset>" + publicKitList.get(i).name + " <red><b>[UNASSIGNED]</b></red>",
                                    "<gray>● Admins have not yet setup this kit yet</gray>"));
                }
            }

            if (player.hasPermission("perplayerkit.admin")) {
                addAdminPublicKitButton(menu.getSlot(i + 18), publicKitList.get(i).id);
            }
        }

        addMainButton(menu.getSlot(53));

        menu.getSlot(53).setItem(createItem(Material.OAK_DOOR, 1, "<red><b>BACK</b></red>"));
        menu.open(player);
    }

    public void addClear(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                Menu m = info.getClickedMenu();
                for (int i = 0; i < 41; i++) {
                    m.getSlot(i).setItem((org.bukkit.inventory.ItemStack) null);
                }
            }
        });
    }

    public void addClear(Slot slot, int start, int end) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                Menu m = info.getClickedMenu();
                for (int i = start; i < end; i++) {
                    m.getSlot(i).setItem((org.bukkit.inventory.ItemStack) null);
                }
            }
        });
    }

    public void addClearKit(Slot slot, UUID target, int slotNum) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                KitManager.get().deleteKit(target, slotNum);
                dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "kit-deleted", "%slot%",
                        String.valueOf(slotNum));
                SoundManager.playSuccess(player);
                kitDeletionFlag.add(player.getUniqueId());
                info.getClickedMenu().close();
                SoundManager.playCloseGui(player);
            }
        });
    }

    public void addClearEnderchest(Slot slot, UUID target, int slotNum) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                KitManager.get().deleteEnderchest(target, slotNum);
                dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "ec-deleted", "%slot%",
                        String.valueOf(slotNum));
                SoundManager.playSuccess(player);
                kitDeletionFlag.add(player.getUniqueId());
                info.getClickedMenu().close();
                SoundManager.playCloseGui(player);
            }
        });
    }

    public void addPublicKitButton(Slot slot, String id) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType() == ClickType.LEFT) {
                KitManager.get().loadPublicKit(player, id);
                info.getClickedMenu().close();
            } else if (info.getClickType() == ClickType.RIGHT) {
                Menu m = ViewPublicKitMenu(player, id);
                if (m != null) {
                    m.open(player);
                }
            }
        });
    }

    public void addAdminPublicKitButton(Slot slot, String id) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                OpenPublicKitEditor(player, id);
                return;
            }
            if (info.getClickType() == ClickType.LEFT) {
                KitManager.get().loadPublicKit(player, id);
            } else if (info.getClickType() == ClickType.RIGHT) {
                Menu m = ViewPublicKitMenu(player, id);
                if (m != null) {
                    m.open(player);
                }
            }
        });
    }

    public void addMainButton(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            OpenMainMenu(player);
        });
    }

    public void addKitRoom(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            OpenKitRoom(player);
            BroadcastManager.get().broadcastPlayerOpenedKitRoom(player);
        });
    }

    public void addKitRoom(Slot slot, int page) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            OpenKitRoom(player, page);
        });
    }

    public void addPublicKitMenu(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            OpenPublicKitMenu(player);
        });
    }

    public void addKitRoomSaveButton(Slot slot, int page) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isRightClick() && info.getClickType().isShiftClick()) {
                ItemStack[] data = new ItemStack[45];
                for (int i = 0; i < 45; i++) {
                    data[i] = player.getOpenInventory().getTopInventory().getItem(i);
                }
                KitRoomDataManager.get().setKitRoom(page, data);
                KitRoomDataManager.get().saveToDBAsync();
                dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "menu-saved");
                SoundManager.playSuccess(player);
            }
        });
    }

    public void addRepairButton(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            BroadcastManager.get().broadcastPlayerRepaired(player);
            PlayerUtil.repairAll(player);
            player.updateInventory();
            SoundManager.playSuccess(player);
        });
    }

    public void addClearButton(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isShiftClick()) {
                player.getInventory().clear();
                dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "inventory-cleared");
                SoundManager.playSuccess(player);
            }
        });
    }

    public void addImport(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            Menu m = info.getClickedMenu();
            ItemStack[] inv;
            if (filterItemsOnImport) {
                inv = ItemFilter.get().filterItemStack(player.getInventory().getContents());
            } else {
                inv = player.getInventory().getContents();
            }
            for (int i = 0; i < 41; i++) {
                m.getSlot(i).setItem(inv[i]);
            }
        });
    }

    public void addImportEC(Slot slot) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            Menu m = info.getClickedMenu();
            ItemStack[] inv;
            if (filterItemsOnImport) {
                inv = ItemFilter.get().filterItemStack(player.getEnderChest().getContents());
            } else {
                inv = player.getEnderChest().getContents();
            }
            for (int i = 0; i < 27; i++) {
                m.getSlot(i + 9).setItem(inv[i]);
            }
        });
    }

    public void addEdit(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isLeftClick() || info.getClickType().isRightClick()) {
                OpenKitMenu(player, i);
            }
        });
    }

    public void addEditEC(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType().isLeftClick() || info.getClickType().isRightClick()) {
                OpenECKitKenu(player, i);
            }
        });
    }

    public void addLoad(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType() == ClickType.LEFT || info.getClickType() == ClickType.SHIFT_LEFT) {
                KitManager.get().loadKit(player, i);
                info.getClickedMenu().close();
                SoundManager.playCloseGui(player);
            }
        });
    }

    public void addEditLoad(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType() == ClickType.LEFT || info.getClickType() == ClickType.SHIFT_LEFT) {
                KitManager.get().loadKit(player, i);
                info.getClickedMenu().close();
            } else if (info.getClickType() == ClickType.RIGHT || info.getClickType() == ClickType.SHIFT_RIGHT) {
                OpenKitMenu(player, i);
            }
        });
    }

    public void addEditLoadEC(Slot slot, int i) {
        slot.setClickHandler((player, info) -> {
            SoundManager.playClick(player);
            if (info.getClickType() == ClickType.LEFT || info.getClickType() == ClickType.SHIFT_LEFT) {
                KitManager.get().loadEnderchest(player, i);
                info.getClickedMenu().close();
            } else if (info.getClickType() == ClickType.RIGHT || info.getClickType() == ClickType.SHIFT_RIGHT) {
                OpenECKitKenu(player, i);
            }
        });
    }

    public Menu createKitMenu(int slot) {
        return ChestMenu.builder(6).title(" " + slot + ". ᴋɪᴛɪ ᴇᴅɪᴛʟᴇ").build();
    }

    public Menu createPublicKitMenu(String id) {
        return ChestMenu.builder(6).title("ʜᴀᴢɪʀ ᴋɪᴛʟᴇʀ: " + id).build();
    }

    public Menu createECMenu(int slot) {
        return ChestMenu.builder(6).title("ᴇɴᴅᴇʀ ᴄʜᴇꜱᴛ: " + slot).build();
    }

    public Menu createInspectMenu(int slot, String playerName) {
        return ChestMenu.builder(6).title(" " + playerName + slot + ". ᴋɪᴛɪ").build();
    }

    public Menu createInspectEcMenu(int slot, String playerName) {
        return ChestMenu.builder(6).title(" " + playerName + slot + "ᴇɴᴅᴇʀ ᴄʜᴇꜱᴛɪ ").build();
    }

    public Menu createMainMenu(Player p) {
        return ChestMenu.builder(6).title("ᴋɪᴛ ᴍᴇɴü ").build();
    }

    public Menu createKitRoom() {
        return ChestMenu.builder(6).title("ᴋɪᴛ ᴏᴅᴀꜱɪ").redraw(true).build();
    }

    public void allowModification(Slot slot) {
        ClickOptions options = ClickOptions.ALLOW_ALL;
        slot.setClickOptions(options);
    }

    private String getPlayerName(UUID uuid) {
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null) {
            return onlinePlayer.getName();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String name = offlinePlayer.getName();
        return name != null ? name : uuid.toString();
    }
}
