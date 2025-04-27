package org.DBsGameplay.oreBreaker.command;

import org.DBsGameplay.oreBreaker.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player p)) return true;

        if (!p.hasPermission("orebreaker.setspawn")) return true;

        if (strings.length == 0) {
            Location pLoc = p.getLocation();

            Main.getInstance().getConfig().set("spawn", pLoc);
            Main.getInstance().saveConfig();

            p.sendMessage("§aDu hast dein spawn gesetzt.");
        }else {
            p.sendMessage("§cVerwendung§7: /setspawn");
        }
        return false;
    }
}
