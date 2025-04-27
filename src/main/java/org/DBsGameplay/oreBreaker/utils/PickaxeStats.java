package org.DBsGameplay.oreBreaker.utils;

import org.DBsGameplay.oreBreaker.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PickaxeStats {

    public Main plugin = Main.getInstance();
    private final FileConfiguration config = Main.getInstance().getConfig();

    // Setter-Methoden für jedes Attribut
    public void setLevel(Player player, int level) {
        String playerPath = "players." + player.getUniqueId();
        config.set(playerPath + ".level", level);
        plugin.saveConfig();
    }

    public void setPrestigeLevel(Player player, int prestigeLevel) {
        String playerPath = "players." + player.getUniqueId();
        config.set(playerPath + ".prestigeLevel", prestigeLevel);
        plugin.saveConfig();
    }

    public void setBlocksMined(Player player, int blocksMined) {
        String playerPath = "players." + player.getUniqueId();
        config.set(playerPath + ".blocksMined", blocksMined);
        plugin.saveConfig();
    }

    public void setAbbaurate(Player player, int abbaurate) {
        String playerPath = "players." + player.getUniqueId();
        config.set(playerPath + ".abbaurate", abbaurate);
        plugin.saveConfig();
    }

    public void setEfficiency(Player player, int efficiency) {
        String playerPath = "players." + player.getUniqueId();
        config.set(playerPath + ".efficiency", efficiency);
        plugin.saveConfig();
    }

    public void setSpeed(Player player, int speed) {
        String playerPath = "players." + player.getUniqueId();
        config.set(playerPath + ".speed", speed);
        plugin.saveConfig();
    }

    public void setBlockabbau(Player player, int value) {
        String playerPath = "players." + player.getUniqueId();
        config.set(playerPath + ".blockabbau", value);
        plugin.saveConfig();
    }

    // Getter-Methoden (unverändert)
    public int getLevel(Player player) {
        return config.getInt("players." + player.getUniqueId() + ".level", 0);
    }

    public int getPrestigeLevel(Player player) {
        return config.getInt("players." + player.getUniqueId() + ".prestigeLevel", 0);
    }

    public int getBlocksMined(Player player) {
        return config.getInt("players." + player.getUniqueId() + ".blocksMined", 0);
    }

    public int getAbbaurate(Player player) {
        return config.getInt("players." + player.getUniqueId() + ".abbaurate", 1);
    }

    public int getBlockabbau(Player player) {
        return config.getInt("players." + player.getUniqueId() + ".blockabbau", 0);
    }

    public int getEfficiency(Player player) {
        return config.getInt("players." + player.getUniqueId() + ".efficiency", 0);
    }

    public int getSpeed(Player player) {
        return config.getInt("players." + player.getUniqueId() + ".speed", 0);
    }

}