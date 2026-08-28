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
package dev.noah.perplayerkit.commands;

import dev.noah.perplayerkit.TrKit;
import dev.noah.perplayerkit.util.MessageManager;
import dev.noah.perplayerkit.util.importutil.KitsXImporter;
import dev.noah.perplayerkit.KitRoomDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PerPlayerKitCommand implements CommandExecutor, TabCompleter {


    private Plugin plugin;
    public PerPlayerKitCommand(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            // Mesaj Türkçeleştirildi ve Prefix eklendi
            sender.sendMessage("<green><b>mKit <gray>» <white><red>Eksik argüman!");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (sender instanceof org.bukkit.entity.Player) {
                    if (!sender.hasPermission("perplayerkit.admin")) {
                        MessageManager.get().sendMessage((org.bukkit.entity.Player) sender, "no-permission");
                        return true;
                    }
                }
                
                // Kaydetme işlemi
                KitRoomDataManager.get().saveToDBAsync();
                
                ((TrKit) plugin).reloadPlugin();
                
                if (sender instanceof org.bukkit.entity.Player) {
                    MessageManager.get().sendMessage((org.bukkit.entity.Player) sender, "reload-success");
                } else {
                    sender.sendMessage("Configuration and messages reloaded successfully.");
                }
                return true;
            case "about":
                // Mesaj Türkçeleştirildi ve Prefix eklendi
                sender.sendMessage("<green><b>mKit <gray>» <white><green>mKit, oyuncuların kendi kitlerine sahip olmalarını sağlayan bir eklentidir.");
                return true;
            case "import":
                if (args.length < 2) {
                    // Mesaj Türkçeleştirildi ve Prefix eklendi
                    sender.sendMessage("<green><b>mKit <gray>» <white><red>İçe aktarma türü belirtilmedi!");
                    return true;
                }

                switch (args[1].toLowerCase()) {
                    case "kitsx":
                        // Mesajlar Türkçeleştirildi ve Prefix eklendi
                        sender.sendMessage("<green><b>mKit <gray>» <white><green>İçe aktarma başlatılıyor...");
                        KitsXImporter importer = new KitsXImporter(plugin,sender);
                        if(!importer.checkForFiles()){
                            sender.sendMessage("<green><b>mKit <gray>» <white><red>İçe aktarılacak dosyalar eksik.");
                            sender.sendMessage("<green><b>mKit <gray>» <white><red>KitsX veri klasörünü TrKit klasörüne kopyaladığınızdan emin olun.");
                        }
                        importer.importFiles();
                        sender.sendMessage("<green><b>mKit <gray>» <white><green>KitsX veri aktarımı tamamlandı/denendi!");

                        break;
                    default:
                        // Mesaj Türkçeleştirildi ve Prefix eklendi
                        sender.sendMessage("<green><b>mKit <gray>» <white><red>Geçersiz içe aktarma türü!");
                        break;
                }
                return true;
            default:
                // Mesaj Türkçeleştirildi ve Prefix eklendi
                sender.sendMessage("<green><b>mKit <gray>» <white><red>Geçersiz alt komut!");
                return true;

        }
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 1) {
            return List.of("about", "import", "reload");
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("import")) {
            return List.of("kitsx");
        }

        return null;
    }
}
