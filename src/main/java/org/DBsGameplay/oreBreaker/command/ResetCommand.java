package org.DBsGameplay.oreBreaker.command;

import org.DBsGameplay.oreBreaker.listeners.Events;
import org.DBsGameplay.oreBreaker.utils.MinionStats;
import org.DBsGameplay.oreBreaker.utils.MinionType;
import org.DBsGameplay.oreBreaker.utils.PickaxeStats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetCommand implements CommandExecutor {
    private final PickaxeStats pickaxeStats = new PickaxeStats();
    private final MinionStats minionStats = new MinionStats();
    private final Events events = new Events();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return true;


        if (strings.length != 0) {
            player.sendMessage("Verwendung: /reset");
            return true;
        }

        pickaxeStats.setAbbaurate(player, 1);
        pickaxeStats.setBlockabbau(player, 0);
        pickaxeStats.setEfficiency(player, 0);
        pickaxeStats.setLevel(player, 0);
        pickaxeStats.setSpeed(player, 0);
        pickaxeStats.setBlocksMined(player, 0);
        pickaxeStats.setPrestigeLevel(player, 0);


        for (MinionType minionType : MinionType.values()) {
            minionStats.setMinionUnlocked(player, minionType, false);
            minionStats.setMiningLevel(player, minionType, 1);
            minionStats.setUnlockedBlocks(player, minionType, 1);
        }

        player.getInventory().clear();
        player.getInventory().addItem(events.getPickaxe(player));

        return false;
    }
}
