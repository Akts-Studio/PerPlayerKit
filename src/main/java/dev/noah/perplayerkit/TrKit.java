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

import dev.noah.perplayerkit.commands.*;
import dev.noah.perplayerkit.commands.extracommands.RepairCommand;
import dev.noah.perplayerkit.commands.extracommands.HealCommand;
import dev.noah.perplayerkit.commands.RegearCommand;
import dev.noah.perplayerkit.listeners.RegearListener;
import dev.noah.perplayerkit.commands.tabcompleters.ECSlotTabCompleter;
import dev.noah.perplayerkit.commands.tabcompleters.KitSlotTabCompleter;
import dev.noah.perplayerkit.listeners.*;
import dev.noah.perplayerkit.listeners.antiexploit.CommandListener;
import dev.noah.perplayerkit.listeners.antiexploit.ShulkerDropItemsListener;
import dev.noah.perplayerkit.listeners.features.OldDeathDropListener;
import dev.noah.perplayerkit.storage.StorageManager;
import dev.noah.perplayerkit.storage.StorageSelector;
import dev.noah.perplayerkit.storage.exceptions.StorageConnectionException;
import dev.noah.perplayerkit.storage.exceptions.StorageOperationException;
import dev.noah.perplayerkit.util.BackupManager;
import dev.noah.perplayerkit.util.BroadcastManager;
import dev.noah.perplayerkit.util.MessageManager;
import dev.noah.perplayerkit.util.StyleManager;
import com.tcoded.folialib.FoliaLib;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

public final class TrKit extends JavaPlugin {

    public static Plugin plugin;
    public static FoliaLib foliaLib;
    public static StorageManager storageManager;
    private BackupManager backupManager;

    public static Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {

        int bstatsId = 24380;
        Metrics metrics = new Metrics(this, bstatsId);

        plugin = this;
        foliaLib = new FoliaLib(this);
        ConfigManager configManager = new ConfigManager(this);
        configManager.loadConfig();

        new MessageManager(this);
        new StyleManager(this);

        new ItemFilter(this);
        new BroadcastManager(this);

        new KitManager(this);
        new KitShareManager(this);
        new KitRoomDataManager(this);

        loadPublicKitsIdsFromConfig();
        getLogger().info("Public Kit Configuration Loaded");

        String dbType = this.getConfig().getString("storage.type");

        if (dbType == null) {
            this.getLogger().warning("Database type not found in config, fix your config to continue!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        storageManager = new StorageSelector(this, dbType).getDbManager();
        this.getLogger().info("Using storage type: " + storageManager.getClass().getName());

        if (storageManager == null) {
            this.getLogger().warning("Database error occurred, please check your config!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        attemptDatabaseConnection(true);

        try {
            storageManager.init();
        } catch (StorageOperationException e) {
            this.getLogger().warning("Failed to initialize the database. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize backup system for file-based storage methods
        if (isFileBasedStorage(dbType)) {
            backupManager = new BackupManager(this);
            if (backupManager.isEnabled()) {
                getLogger().info("Backup system initialized for file-based storage");
            } else {
                getLogger().info("Backup system disabled in configuration");
            }
        } else {
            getLogger().info("Backup system not needed for non-file-based storage: " + dbType);
        }

        foliaLib.getImpl().runTimerAsync(new Runnable() {
            @Override
            public void run() {
                if (storageManager.isConnected()) {
                    try {
                        storageManager.keepAlive();
                    } catch (StorageConnectionException e) {
                        TrKit.this.getLogger().warning("Database keep alive failed: " + e.getMessage());
                    }
                } else {
                    TrKit.this.getLogger().warning("Database connection failed. Attempting to reconnect.");
                    attemptDatabaseConnection(false);
                }
            }
        }, 30 * 20L, 30 * 20L);

        loadDatabaseData();
        KitManager.get().sanitizeAllStoredKitsByOnlineVipPermissions();
        getLogger().info("Database data loaded");


        // REGISTER THINGS START
        KitSlotTabCompleter kitSlotTabCompleter = new KitSlotTabCompleter();
        ECSlotTabCompleter ecSlotTabCompleter = new ECSlotTabCompleter();

        this.getCommand("kit").setExecutor(new MainMenuCommand(plugin));

        this.getCommand("sharekit").setExecutor(new ShareKitCommand());
        this.getCommand("sharekit").setTabCompleter(kitSlotTabCompleter);

        this.getCommand("shareec").setExecutor(new ShareECKitCommand());
        this.getCommand("shareec").setTabCompleter(ecSlotTabCompleter);

        this.getCommand("copykit").setExecutor(new CopyKitCommand());

        KitRoomCommand kitRoomCommand = new KitRoomCommand();
        this.getCommand("kitroomadmin").setExecutor(kitRoomCommand);
        this.getCommand("kitroomadmin").setTabCompleter(kitRoomCommand);

        this.getCommand("swapkit").setExecutor(new SwapKitCommand());
        this.getCommand("swapkit").setTabCompleter(kitSlotTabCompleter);

        this.getCommand("deletekit").setExecutor(new DeleteKitCommand());
        this.getCommand("deletekit").setTabCompleter(kitSlotTabCompleter);

        this.getCommand("inspectkit").setExecutor(new InspectKitCommand(plugin));
        this.getCommand("inspectkit").setTabCompleter(new InspectKitCommand(plugin));

        this.getCommand("inspectec").setExecutor(new InspectEcCommand(plugin));
        this.getCommand("inspectec").setTabCompleter(new InspectEcCommand(plugin));

        this.getCommand("enderchest").setExecutor(new EnderchestCommand());

        SavePublicKitCommand savePublicKitCommand = new SavePublicKitCommand();
        this.getCommand("savepublickit").setExecutor(savePublicKitCommand);
        this.getCommand("savepublickit").setTabCompleter(savePublicKitCommand);

        PublicKitCommand publicKitCommand = new PublicKitCommand(plugin);
        this.getCommand("publickit").setExecutor(publicKitCommand);
        this.getCommand("publickit").setTabCompleter(publicKitCommand);

        for (int i = 1; i <= 9; i++) {
            this.getCommand("k" + i).setExecutor(new ShortKitCommand());
        }

        for (int i = 1; i <= 9; i++) {
            this.getCommand("ec" + i).setExecutor(new ShortECCommand());
        }

        this.getCommand("heal").setExecutor(new HealCommand());
        this.getCommand("repair").setExecutor(new RepairCommand());
        this.getCommand("trkit").setExecutor(new PerPlayerKitCommand(this));

        KitBroadcastCommand kitBroadcastCommand = new KitBroadcastCommand(this);
        this.getCommand("kitbroadcast").setExecutor(kitBroadcastCommand);
        this.getCommand("kitbroadcast").setTabCompleter(kitBroadcastCommand);

        this.getCommand("rekit").setExecutor(new RekitCommand());
        this.getCommand("kitroom").setExecutor(new OpenKitRoomCommand(this));

        RekitToggleCommand rekitToggleCommand = new RekitToggleCommand();
        this.getCommand("rekittoggle").setExecutor(rekitToggleCommand);
        this.getCommand("rekittoggle").setTabCompleter(rekitToggleCommand);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new dev.noah.perplayerkit.util.PerPlayerKitExpansion(this).register();
        }

        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new KitMenuCloseListener(), this);
        Bukkit.getPluginManager().registerEvents(new AutoRekitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AboutCommandListener(), this);

        RegearListener regearListener = new RegearListener(this);
        Bukkit.getPluginManager().registerEvents(regearListener, this);
        this.getCommand("regear").setExecutor(new RegearCommand(this, regearListener));

        // features
        if (getConfig().getBoolean("feature.old-death-drops", false)) {
            Bukkit.getPluginManager().registerEvents(new OldDeathDropListener(), this);
        }

        if (getConfig().getBoolean("anti-exploit.block-spaces-in-commands", false)) {
            Bukkit.getPluginManager().registerEvents(new CommandListener(), this);
        }

        if (getConfig().getBoolean("anti-exploit.prevent-shulkers-dropping-items", false)) {
            Bukkit.getPluginManager().registerEvents(new ShulkerDropItemsListener(), this);
        }

        // REGISTER THINGS END

        BroadcastManager.get().startScheduledBroadcast();

    }

    @Override
    public void onDisable() {
        closeDatabaseConnection();

        // Shutdown backup manager if it exists
        if (backupManager != null) {
            backupManager.shutdown();
        }
    }

    /**
     * Check if the storage type is file-based (requires backups)
     * 
     * @param storageType The storage type from configuration
     * @return true if file-based storage, false otherwise
     */
    private boolean isFileBasedStorage(String storageType) {
        return storageType.equalsIgnoreCase("sqlite") ||
                storageType.equalsIgnoreCase("yml") ||
                storageType.equalsIgnoreCase("yaml");
    }

    private void loadPublicKitsIdsFromConfig() {
        // generate list of public kits from the config
        ConfigurationSection publicKitsSection = getConfig().getConfigurationSection("publickits");

        if (publicKitsSection == null) {
            this.getLogger().warning("No public kits found in config!");
        } else {

            publicKitsSection.getKeys(false).forEach(key -> {
                String name = getConfig().getString("publickits." + key + ".name");
                Material icon = Material.valueOf(getConfig().getString("publickits." + key + ".icon"));
                PublicKit kit = new PublicKit(key, name, icon);
                KitManager.get().getPublicKitList().add(kit);
            });
        }
    }

    private void loadDatabaseData() {
        KitRoomDataManager.get().loadFromDB();
        KitManager.get().getPublicKitList().forEach(kit -> KitManager.get().loadPublicKitFromDB(kit.id));
        Bukkit.getOnlinePlayers().forEach(player -> KitManager.get().loadPlayerDataFromDB(player.getUniqueId()));

    }

    private void attemptDatabaseConnection(boolean disableOnFail) {
        try {
            storageManager.connect();
            if (!storageManager.isConnected()) {
                throw new StorageConnectionException("Expected to be connected to the database, but failed.");
            }
        } catch (StorageConnectionException e) {
            if (disableOnFail) {
                this.getLogger().warning("Database connection failed: " + e.getMessage());
                this.getLogger().warning("Disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(this);
            } else {
                this.getLogger().warning("Database connection failed: " + e.getMessage());
            }
        }
    }

    private void closeDatabaseConnection() {
        try {
            storageManager.close();
        } catch (StorageConnectionException e) {
            // retry once
            try {
                storageManager.close();
            } catch (StorageConnectionException ex) {
                this.getLogger().warning("Failed to close the database connection: " + e.getMessage());
            }
        }
    }

    public void reloadPlugin() {
        this.reloadConfig();
        new ConfigManager(this).loadConfig();
        
        BroadcastManager.get().reloadPrefix();
        BroadcastManager.get().startScheduledBroadcast();
        
        KitManager.get().clearPublicKits();
        loadPublicKitsIdsFromConfig();
        
        ItemFilter.get().reloadBlacklist();
        KitManager.get().filterAllLoadedKits();
        
        MessageManager.get().reloadMessages();
    }

}

