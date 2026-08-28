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
package dev.noah.perplayerkit;

import dev.noah.perplayerkit.util.BroadcastManager;
import dev.noah.perplayerkit.util.IDUtil;
import dev.noah.perplayerkit.util.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.entity.Player;
import dev.noah.perplayerkit.util.SoundManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.io.IOException;
import java.util.*;

public class KitManager {
    private static KitManager instance;
    private final TrKit plugin;
    private final HashMap<String, ItemStack[]> kitByKitIDMap;
    private final HashMap<UUID, Integer> lastKitUsedByPlayer;
    private final List<PublicKit> publicKitList;

    public KitManager(TrKit plugin) {
        this.plugin = plugin;
        lastKitUsedByPlayer = new HashMap<>();
        publicKitList = new ArrayList<>();
        kitByKitIDMap = new HashMap<>();
        instance = this;
    }

    public static KitManager get() {
        if (instance == null) {
            throw new IllegalStateException("KitManager not initialized");
        }
        return instance;
    }

    public ItemStack[] getItemStackArrayById(String id) {
        return kitByKitIDMap.get(id);
    }

    public List<PublicKit> getPublicKitList() {
        return publicKitList;
    }

    public void clearPublicKits() {
        publicKitList.clear();
    }

    public int getLastKitLoaded(UUID uuid) {
        if (lastKitUsedByPlayer.containsKey(uuid)) {
            return lastKitUsedByPlayer.get(uuid);
        }
        return -1;
    }

    public boolean savekit(UUID uuid, int slot, ItemStack[] kit) {
        if (Bukkit.getPlayer(uuid) != null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                boolean notEmpty = false;
                for (ItemStack i : kit) {
                    if (i != null) {
                        if (!notEmpty) {
                            notEmpty = true;
                        }
                    }
                }

                stripRestrictedCosmeticsIfNeeded(player, kit);

                if (notEmpty) {
                    if (kit[36] != null) {
                        if (!kit[36].getType().toString().contains("BOOTS")) {
                            kit[36] = null;
                        }
                    }
                    if (kit[37] != null) {
                        if (!kit[37].getType().toString().contains("LEGGINGS")) {
                            kit[37] = null;
                        }
                    }
                    if (kit[38] != null) {
                        if (!(kit[38].getType().toString().contains("CHESTPLATE")
                                || kit[38].getType().toString().contains("ELYTRA"))) {
                            kit[38] = null;
                        }
                    }
                    if (kit[39] != null) {
                        if (!kit[39].getType().toString().contains("HELMET")) {
                            kit[39] = null;
                        }
                    }

                    kitByKitIDMap.put(IDUtil.getPlayerKitId(uuid, slot), kit);
                    dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "kit-saved", "%slot%", String.valueOf(slot));

                    dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runAsync((wrappedTask) -> savePlayerKitToDB(uuid, slot));
                    return true;
                } else {
                    dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "kit-empty");
                }
            }
        }
        return false;
    }

    public boolean savePublicKit(Player player, String publickit, ItemStack[] kit) {
        boolean notEmpty = false;
        for (ItemStack i : kit) {
            if (i != null) {
                if (!notEmpty) {
                    notEmpty = true;
                }
            }
        }

        stripRestrictedCosmeticsIfNeeded(player, kit);

        if (notEmpty) {
            if (kit[36] != null) {
                if (!kit[36].getType().toString().contains("BOOTS")) {
                    kit[36] = null;
                }
            }
            if (kit[37] != null) {
                if (!kit[37].getType().toString().contains("LEGGINGS")) {
                    kit[37] = null;
                }
            }
            if (kit[38] != null) {
                if (!(kit[38].getType().toString().contains("CHESTPLATE")
                        || kit[38].getType().toString().contains("ELYTRA"))) {
                    kit[38] = null;
                }
            }
            if (kit[39] != null) {
                if (!kit[39].getType().toString().contains("HELMET")) {
                    kit[39] = null;
                }
            }

            kitByKitIDMap.put(IDUtil.getPublicKitId(publickit), kit);
            dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "public-kit-saved", "%id%", publickit);

            dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runAsync((wrappedTask) -> savePublicKitToDB(publickit));
            return true;
        } else {
            dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "kit-empty");
        }
        return false;
    }

    public boolean savePublicKit(String id, ItemStack[] kit) {
        boolean notEmpty = false;
        for (ItemStack i : kit) {
            if (i != null) {
                if (!notEmpty) {
                    notEmpty = true;
                }
            }
        }

        if (notEmpty) {
            if (kit[36] != null) {
                if (!kit[36].getType().toString().contains("BOOTS")) {
                    kit[36] = null;
                }
            }
            if (kit[37] != null) {
                if (!kit[37].getType().toString().contains("LEGGINGS")) {
                    kit[37] = null;
                }
            }
            if (kit[38] != null) {
                if (!(kit[38].getType().toString().contains("CHESTPLATE")
                        || kit[38].getType().toString().contains("ELYTRA"))) {
                    kit[38] = null;
                }
            }
            if (kit[39] != null) {
                if (!kit[39].getType().toString().contains("HELMET")) {
                    kit[39] = null;
                }
            }

            kitByKitIDMap.put(IDUtil.getPublicKitId(id), kit);
            return true;
        }
        return false;
    }

    public boolean saveEC(UUID uuid, int slot, ItemStack[] kit) {
        if (Bukkit.getPlayer(uuid) != null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                boolean notEmpty = false;
                for (ItemStack i : kit) {
                    if (i != null) {
                        if (!notEmpty) {
                            notEmpty = true;
                        }
                    }
                }

                if (notEmpty) {
                    stripRestrictedCosmeticsIfNeeded(player, kit);
                    kitByKitIDMap.put(IDUtil.getECId(uuid, slot), kit);
                    dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "ec-saved", "%slot%", String.valueOf(slot));
                    dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runAsync((wrappedTask) -> saveEnderchestToDB(uuid, slot));
                    return true;
                } else {
                    dev.noah.perplayerkit.util.MessageManager.get().sendMessage(player, "ec-empty");
                }
            }
        }
        return false;
    }

    public boolean savekit(UUID uuid, int slot, ItemStack[] kit, boolean silent) {
        if (silent) {
            if (Bukkit.getPlayer(uuid) != null) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    boolean notEmpty = false;
                    for (ItemStack i : kit) {
                        if (i != null) {
                            if (!notEmpty) {
                                notEmpty = true;
                            }
                        }
                    }

                    if (notEmpty) {
                        stripRestrictedCosmeticsIfNeeded(player, kit);
                        if (kit[36] != null) {
                            if (!kit[36].getType().toString().contains("BOOTS")) {
                                kit[36] = null;
                            }
                        }
                        if (kit[37] != null) {
                            if (!kit[37].getType().toString().contains("LEGGINGS")) {
                                kit[37] = null;
                            }
                        }
                        if (kit[38] != null) {
                            if (!(kit[38].getType().toString().contains("CHESTPLATE")
                                    || kit[38].getType().toString().contains("ELYTRA"))) {
                                kit[38] = null;
                            }
                        }
                        if (kit[39] != null) {
                            if (!kit[39].getType().toString().contains("HELMET")) {
                                kit[39] = null;
                            }
                        }

                        kitByKitIDMap.put(IDUtil.getPlayerKitId(uuid, slot), ItemFilter.get().filterItemStack(kit));
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Kit Boş Olamaz!");
                    }
                }
            }
            return false;
        } else {
            return savekit(uuid, slot, kit);
        }
    }

    public boolean regearKit(Player player, int slot) {
        UUID uuid = player.getUniqueId();
        if (kitByKitIDMap.get(IDUtil.getPlayerKitId(uuid, slot)) == null) {
            return false;
        }

        boolean invertWhitelist = plugin.getConfig().getBoolean("regear.invert-whitelist", false);
        Set<String> whitelist = new HashSet<>(plugin.getConfig().getStringList("regear.whitelist"));

        ItemStack[] kit = kitByKitIDMap.get(IDUtil.getPlayerKitId(uuid, slot));
        ItemStack[] playerInventory = player.getInventory().getContents();
        for (int i = 0; i < Math.min(playerInventory.length, kit.length); i++) {
            if (kit[i] == null) {
                continue;
            }

            if (invertWhitelist) {
                if (whitelist.contains(kit[i].getType().toString())) {
                    continue;
                }
            } else {
                if (!whitelist.contains(kit[i].getType().toString())) {
                    continue;
                }
            }

            if (playerInventory[i] == null || playerInventory[i].getType().isAir()
                    || playerInventory[i].getType() == kit[i].getType()) {
                playerInventory[i] = kit[i];
                continue;
            }
        }
        player.getInventory().setContents(playerInventory);
        return true;
    }

    private boolean loadKitInternal(Player player, String kitId, String notFoundMessage, boolean isEnderChest,
            Runnable afterLoad) {
        if (player == null) {
            return false;
        }

        ItemStack[] kit = kitByKitIDMap.get(kitId);
        if (kit == null) {
            if (notFoundMessage != null) {
                player.sendMessage(ChatColor.RED + notFoundMessage);
                SoundManager.playFailure(player);
            }
            return false;
        }

        if (isEnderChest) {
            player.getEnderChest().setContents(kit);
        } else {
            player.getInventory().setContents(kit);
        }

        if (afterLoad != null) {
            afterLoad.run();
        }
        SoundManager.playSuccess(player);
        applyKitLoadEffects(player, isEnderChest);
        return true;
    }

    public boolean loadKit(Player player, int slot) {
        return loadKitInternal(player, IDUtil.getPlayerKitId(player.getUniqueId(), slot),
                "Kit " + slot + " does not exist!", false, () -> {
                    BroadcastManager.get().broadcastPlayerLoadedPrivateKit(player);
                    player.sendMessage(org.bukkit.ChatColor.GREEN + "" + slot + ". Kit Yüklendi!");
                    lastKitUsedByPlayer.put(player.getUniqueId(), slot);

                    // Persist to PDC
                    player.getPersistentDataContainer().set(dev.noah.perplayerkit.commands.RekitToggleCommand.LAST_KIT_KEY,
                            org.bukkit.persistence.PersistentDataType.INTEGER, slot);
                });
    }

    public boolean loadKitSilent(Player player, int slot) {
        return loadKitInternal(player, IDUtil.getPlayerKitId(player.getUniqueId(), slot), null, false, null);
    }

    public boolean loadPublicKit(Player player, String id) {
        return loadKitInternal(player, IDUtil.getPublicKitId(id), "Kit Bulunamadı!", false, () -> {
            BroadcastManager.get().broadcastPlayerLoadedPublicKit(player);
            player.sendMessage(ChatColor.GREEN + "Public Kit Yüklendi!");
            player.sendMessage(
                    ChatColor.GRAY + "Bu kiti kit düzenleyicisine aktararak kendi kitinizi kaydedebilirsiniz.");
        });
    }

    public boolean loadPublicKitSilent(Player player, String id) {
        return loadKitInternal(player, IDUtil.getPublicKitId(id), null, false, null);
    }

    public boolean loadEnderchest(Player player, int slot) {
        return loadKitInternal(player, IDUtil.getECId(player.getUniqueId(), slot),
                "Enderchest " + slot + " Bulunamadı!", true, () -> {
                    BroadcastManager.get().broadcastPlayerLoadedEnderChest(player);
                    player.sendMessage(ChatColor.GREEN + "" + slot + ". Enderchest Yüklendi!");
                });
    }

    public boolean loadEnderchestSilent(Player player, int slot) {
        return loadKitInternal(player, IDUtil.getECId(player.getUniqueId(), slot), null, true, null);
    }

    public boolean loadLastKit(Player player) {
        if (lastKitUsedByPlayer.containsKey(player.getUniqueId())) {
            return loadKit(player, lastKitUsedByPlayer.get(player.getUniqueId()));
        }

        // Try to load from PDC if not in memory
        if (player.getPersistentDataContainer().has(dev.noah.perplayerkit.commands.RekitToggleCommand.LAST_KIT_KEY,
                org.bukkit.persistence.PersistentDataType.INTEGER)) {
            int slot = player.getPersistentDataContainer().get(
                    dev.noah.perplayerkit.commands.RekitToggleCommand.LAST_KIT_KEY,
                    org.bukkit.persistence.PersistentDataType.INTEGER);
            return loadKit(player, slot);
        }
        return false;
    }

    public boolean hasKit(UUID uuid, int slot) {
        return kitByKitIDMap.get(IDUtil.getPlayerKitId(uuid, slot)) != null;
    }

    public boolean hasEC(UUID uuid, int slot) {
        return kitByKitIDMap.get(IDUtil.getECId(uuid, slot)) != null;
    }

    public ItemStack[] getPlayerEC(UUID uuid, int slot) {
        return kitByKitIDMap.get(IDUtil.getECId(uuid, slot));
    }

    public ItemStack[] getPlayerKit(UUID uuid, int slot) {
        return kitByKitIDMap.get(IDUtil.getPlayerKitId(uuid, slot));
    }

    public boolean hasPublicKit(String id) {
        return kitByKitIDMap.get(IDUtil.getPublicKitId(id)) != null;
    }

    public ItemStack[] getPublicKit(String id) {
        return kitByKitIDMap.get(IDUtil.getPublicKitId(id));
    }

    public void loadPlayerDataFromDB(UUID uuid) {
        for (int slot = 1; slot < 10; slot++) {
            String data = TrKit.storageManager.getKitDataByID(IDUtil.getPlayerKitId(uuid, slot));
            if (!data.equalsIgnoreCase("error")) {
                try {
                    ItemStack[] kit = Serializer.itemStackArrayFromBase64(data);
                    kitByKitIDMap.put(IDUtil.getPlayerKitId(uuid, slot), ItemFilter.get().filterItemStack(kit));
                } catch (IOException ignored) {
                }
            }
        }
        for (int slot = 1; slot < 10; slot++) {
            String data = TrKit.storageManager.getKitDataByID(IDUtil.getECId(uuid, slot));
            if (!data.equalsIgnoreCase("error")) {
                try {
                    ItemStack[] kit = Serializer.itemStackArrayFromBase64(data);
                    kitByKitIDMap.put(IDUtil.getECId(uuid, slot), ItemFilter.get().filterItemStack(kit));
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void savePlayerKitsToDB(UUID uuid) {
        for (int i = 1; i < 10; i++) {
            saveKitToDB(IDUtil.getPlayerKitId(uuid, i), true);
            saveKitToDB(IDUtil.getECId(uuid, i), true);
        }
    }

    public void savePlayerKitToDB(UUID uuid, int slot) {
        saveKitToDB(IDUtil.getPlayerKitId(uuid, slot), false);
    }

    public void saveEnderchestToDB(UUID uuid, int slot) {
        saveKitToDB(IDUtil.getECId(uuid, slot), false);
    }

    public void savePublicKitToDB(String id) {
        saveKitToDB(IDUtil.getPublicKitId(id), false);
    }

    private void saveKitToDB(String key, boolean removeAfterSave) {
        if (kitByKitIDMap.get(key) != null) {
            TrKit.storageManager.saveKitDataByID(key,
                    Serializer.itemStackArrayToBase64(ItemFilter.get().filterItemStack(kitByKitIDMap.get(key))));
            if (removeAfterSave) {
                kitByKitIDMap.remove(key);
            }
        }
    }

    public void loadPublicKitFromDB(String id) {
        String data = TrKit.storageManager.getKitDataByID(IDUtil.getPublicKitId(id));
        if (!data.equalsIgnoreCase("error")) {
            try {
                ItemStack[] kit = Serializer.itemStackArrayFromBase64(data);
                kitByKitIDMap.put(IDUtil.getPublicKitId(id), ItemFilter.get().filterItemStack(kit));
            } catch (IOException ignored) {
                plugin.getLogger().info("Error loading public kit " + id);
            }
        }
    }

    public boolean deleteKit(UUID uuid, int slot) {
        if (hasKit(uuid, slot)) {
            kitByKitIDMap.remove(IDUtil.getPlayerKitId(uuid, slot));
            dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runAsync(
                    (wrappedTask) -> TrKit.storageManager.deleteKitByID(IDUtil.getPlayerKitId(uuid, slot)));
            return true;
        }
        return false;
    }

    public boolean deleteEnderchest(UUID uuid, int slot) {
        if (hasEC(uuid, slot)) {
            kitByKitIDMap.remove(IDUtil.getECId(uuid, slot));
            dev.noah.perplayerkit.TrKit.foliaLib.getImpl().runAsync(
                    (wrappedTask) -> TrKit.storageManager.deleteKitByID(IDUtil.getECId(uuid, slot)));
            return true;
        }
        return false;
    }

    private void stripRestrictedCosmeticsIfNeeded(Player player, ItemStack[] kit) {
        boolean hasVip = player.hasPermission("perplayerkit.kit.vip")
                || player.hasPermission("perplayerkit.ec.vip");
        boolean canUseTrims = hasVip;
        boolean canUsePatternedShield = hasVip;
        for (ItemStack item : kit) {
            if (item == null || !item.hasItemMeta()) {
                continue;
            }

            if (!canUseTrims && item.getItemMeta() instanceof ArmorMeta armorMeta && armorMeta.hasTrim()) {
                armorMeta.setTrim(null);
                item.setItemMeta(armorMeta);
            }

            if (!canUsePatternedShield && item.getType() == Material.SHIELD
                    && item.getItemMeta() instanceof BlockStateMeta blockStateMeta
                    && blockStateMeta.getBlockState() instanceof Banner banner
                    && !banner.getPatterns().isEmpty()) {
                banner.setPatterns(Collections.emptyList());
                blockStateMeta.setBlockState(banner);
                item.setItemMeta(blockStateMeta);
            }
        }
    }

    public void sanitizeAllStoredKitsByOnlineVipPermissions() {
        int updated = 0;
        for (String kitId : TrKit.storageManager.getAllKitIDs()) {
            if (kitId == null || kitId.length() < 36 || kitId.startsWith("public") || kitId.startsWith("kitroom")) {
                continue;
            }

            UUID playerId;
            try {
                playerId = UUID.fromString(kitId.substring(0, 36));
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            Player onlinePlayer = Bukkit.getPlayer(playerId);
            if (onlinePlayer == null) {
                continue;
            }

            String data = TrKit.storageManager.getKitDataByID(kitId);
            if (data.equalsIgnoreCase("error")) {
                continue;
            }

            try {
                ItemStack[] kit = Serializer.itemStackArrayFromBase64(data);
                String before = Serializer.itemStackArrayToBase64(kit);
                stripRestrictedCosmeticsIfNeeded(onlinePlayer, kit);
                String after = Serializer.itemStackArrayToBase64(kit);
                if (!before.equals(after)) {
                    TrKit.storageManager.saveKitDataByID(kitId, after);
                    updated++;
                }
            } catch (IOException ignored) {
            }
        }

        if (updated > 0) {
            plugin.getLogger().info("Sanitized " + updated + " kit(s): trimmed armor / patterned shield removed for non-VIP online players.");
        }
    }

    private void applyKitLoadEffects(Player player, boolean isEnderChest) {
        if (player.isDead()) {
            return;
        }

        if (isEnderChest) {
            if (plugin.getConfig().getBoolean("feature.heal-on-enderchest-load", false)) {
                player.setHealth(20);
            }
            if (plugin.getConfig().getBoolean("feature.feed-on-enderchest-load", false)) {
                player.setFoodLevel(20);
            }
            if (plugin.getConfig().getBoolean("feature.set-saturation-on-enderchest-load", false)) {
                player.setSaturation(20);
            }
            if (plugin.getConfig().getBoolean("feature.remove-potion-effects-on-enderchest-load", false)) {
                player.getActivePotionEffects()
                        .forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            }
        } else {
            if (plugin.getConfig().getBoolean("feature.set-health-on-kit-load", false)) {
                player.setHealth(20);
            }
            if (plugin.getConfig().getBoolean("feature.set-hunger-on-kit-load", false)) {
                player.setFoodLevel(20);
            }
            if (plugin.getConfig().getBoolean("feature.set-saturation-on-kit-load", false)) {
                player.setSaturation(20);
            }
            if (plugin.getConfig().getBoolean("feature.remove-potion-effects-on-kit-load", false)) {
                player.getActivePotionEffects()
                        .forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            }
        }
    }

    public void filterAllLoadedKits() {
        for (Map.Entry<String, ItemStack[]> entry : kitByKitIDMap.entrySet()) {
            ItemStack[] filtered = ItemFilter.get().filterItemStack(entry.getValue());
            kitByKitIDMap.put(entry.getKey(), filtered);
        }
    }
}
