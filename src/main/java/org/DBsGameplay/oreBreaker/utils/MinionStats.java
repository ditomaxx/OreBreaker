package org.DBsGameplay.oreBreaker.utils;

import org.DBsGameplay.oreBreaker.Main;
import org.bukkit.entity.Player;

public class MinionStats {

    public void setUnlockedBlocks(Player player, MinionType minionType, int amount) {
        Main.getInstance().getConfig().set("minionstats." + player.getUniqueId() + "." + minionType.name() + ".unlockedblocks", amount);
        Main.getInstance().saveConfig();
    }

    public int getUnlockedBlocks(Player player, MinionType minionType) {
        return Main.getInstance().getConfig().getInt("minionstats." + player.getUniqueId() + "." + minionType.name() + ".unlockedblocks", 1);
    }

    public void setMiningLevel(Player player, MinionType minionType, int level) {
        Main.getInstance().getConfig().set("minionstats." + player.getUniqueId() + "." + minionType.name() + ".mininglevel", level);
        Main.getInstance().saveConfig();
    }

    public int getMiningLevel(Player player, MinionType minionType) {
        return Main.getInstance().getConfig().getInt("minionstats." + player.getUniqueId() + "." + minionType.name() + ".mininglevel", 1);
    }

    public void setMinionUnlocked(Player player, MinionType minionType, boolean status) {
        Main.getInstance().getConfig().set("minionstats." + player.getUniqueId() + "." + minionType.name() + ".unlocked", status);
        Main.getInstance().saveConfig();
    }

    public boolean isMinionUnlocked(Player player, MinionType minionType) {
        return Main.getInstance().getConfig().getBoolean("minionstats." + player.getUniqueId() + "." + minionType.name() + ".unlocked", false);
    }
}
