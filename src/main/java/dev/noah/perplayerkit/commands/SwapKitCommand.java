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

import com.google.common.primitives.Ints;
import dev.noah.perplayerkit.KitManager;
import dev.noah.perplayerkit.util.SoundManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SwapKitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            // Mesaj Türkçeleştirildi ve Prefix eklendi
            sender.sendMessage("<green><b>mKit <gray>» <white><red>Bu komutu sadece oyuncular kullanabilir!");
            return true;
        }

        if (args.length != 2) {
            // Mesaj Türkçeleştirildi ve Prefix eklendi
            player.sendMessage("<green><b>mKit <gray>» <white><red>Kullanım: /swapkit <yuva1> <yuva2>");
            SoundManager.playFailure(player);
            return true;
        }

        Integer slot1 = Ints.tryParse(args[0]);
        Integer slot2 = Ints.tryParse(args[1]);

        if (slot1 == null || slot2 == null) {
            // Mesajlar Türkçeleştirildi ve Prefix eklendi
            player.sendMessage("<green><b>mKit <gray>» <white><red>Kullanım: /swapkit <yuva1> <yuva2>");
            player.sendMessage("<green><b>mKit <gray>» <white><red>Geçerli sayılar giriniz");
            SoundManager.playFailure(player);
            return true;
        }

        KitManager kitManager = KitManager.get();
        UUID uuid = player.getUniqueId();

        if (!kitManager.hasKit(uuid, slot1)) {
            // Mesaj Türkçeleştirildi ve Prefix eklendi
            player.sendMessage("<green><b>mKit <gray>» <white><red>Kit " + slot1 + " mevcut değil!");
            SoundManager.playFailure(player);
            return true;
        }

        if (!kitManager.hasKit(uuid, slot2)) {
            // Mesaj Türkçeleştirildi ve Prefix eklendi
            player.sendMessage("<green><b>mKit <gray>» <white><red>Kit " + slot2 + " mevcut değil!");
            SoundManager.playFailure(player);
            return true;
        }

        ItemStack[] tempkit = kitManager.getPlayerKit(uuid, slot1).clone();
        kitManager.savekit(uuid, slot1, kitManager.getPlayerKit(uuid, slot2), true);
        kitManager.savekit(uuid, slot2, tempkit.clone(), true);
        kitManager.saveEnderchestToDB(uuid, slot1);
        kitManager.saveEnderchestToDB(uuid, slot2);

        // Mesaj Türkçeleştirildi ve Prefix eklendi
        player.sendMessage("<green><b>mKit <gray>» <white><green>Kit " + slot1 + " ve " + slot2 + " yer değiştirdi!");
        SoundManager.playSuccess(player);
        return true;
    }
}
