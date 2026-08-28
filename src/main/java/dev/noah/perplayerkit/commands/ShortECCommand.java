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

import dev.noah.perplayerkit.util.DisabledCommand;
import dev.noah.perplayerkit.KitManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ShortECCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            // Mesaj Türkçeleştirildi ve Prefix eklendi
            sender.sendMessage("<green><b>mKit <gray>» <white><red>Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        if (DisabledCommand.isBlockedInWorld(player)) {
            return true;
        }

        UUID uuid = player.getUniqueId();

        if (label.matches("ec[1-9]")) {
            int ecNumber = Integer.parseInt(label.substring(2)); // Extract the number from the label
            if (ecNumber >= 5 && !player.hasPermission("perplayerkit.ec.vip")) {
                player.sendMessage(
                        "\u00a7c\u00a7lVIP\u00a7r \u00a7cbu enderchesti kullanmak i\u00e7in VIP alman\u0131z gereklidir!");
                return true;
            }
            KitManager.get().loadEnderchest(player, ecNumber);
        } else if (label.matches("enderchest[1-9]")) {
            int ecNumber = Integer.parseInt(label.substring(10)); // Extract the number from the label
            if (ecNumber >= 5 && !player.hasPermission("perplayerkit.ec.vip")) {
                player.sendMessage(
                        "\u00a7c\u00a7lVIP\u00a7r \u00a7cbu enderchesti kullanmak i\u00e7in VIP alman\u0131z gereklidir!");
                return true;
            }
            KitManager.get().loadEnderchest(player, ecNumber);
        } else {
            // Mesaj Türkçeleştirildi ve Prefix eklendi
            player.sendMessage("<green><b>mKit <gray>» <white><red>Geçersiz komut etiketi.");
        }

        return true;
    }
}
