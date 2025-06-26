package org.DBsGameplay.oreBreaker.utils;

import org.DBsGameplay.oreBreaker.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.ArmorStand;

public class MinionsManager {

    public void addMinion(MinionType minionType, Location location) {
        if (Main.getInstance().getConfig().contains("minions." + minionType.name())) {
            Main.getInstance().getConfig().getLocation("minions." + minionType.name()).getBlock().setType(Material.AIR);
            Main.getInstance().getConfig().getLocation("minions." + minionType.name()).getNearbyEntities(5,5,5).forEach(entity -> {
                if (entity instanceof ArmorStand) {
                    entity.remove();
                }
            });
        }

        Main.getInstance().getConfig().set("minions." + minionType.name(), location);
        Main.getInstance().saveConfig();

        // Hole den Block und setze ihn als Truhe
        Block block = location.getBlock();
        block.setType(Material.CHEST);

        // Hole BlockData der Truhe und setze die Richtung
        Chest chest = (org.bukkit.block.data.type.Chest) block.getBlockData();
        chest.setFacing(getPlayerDirection(location.getYaw()));
        block.setBlockData(chest);
    }

    private org.bukkit.block.BlockFace getPlayerDirection(float yaw) {
        // Normalisiere Yaw zu 0-360
        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;

        // Konvertiere Yaw zu BlockFace
        if (yaw >= 315 || yaw < 45) return org.bukkit.block.BlockFace.SOUTH;
        if (yaw >= 45 && yaw < 135) return org.bukkit.block.BlockFace.WEST;
        if (yaw >= 135 && yaw < 225) return org.bukkit.block.BlockFace.NORTH;
        return org.bukkit.block.BlockFace.EAST;
    }

    public void removeMinion(Location location) {
        for (String key : Main.getInstance().getConfig().getConfigurationSection("minions").getKeys(false)) {
            Location minionLocation = (Location) Main.getInstance().getConfig().get("minions." + key);

            if (minionLocation.equals(location)) {
                Main.getInstance().getConfig().set("minions." + key, null);
                Main.getInstance().saveConfig();
                location.getBlock().setType(Material.AIR);
                break;
            }
        }
    }

    public boolean isMinion(Location location) {
        if (Main.getInstance().getConfig().getConfigurationSection("minions") == null) return false;

        for (String key : Main.getInstance().getConfig().getConfigurationSection("minions").getKeys(false)) {
            Location minionLocation = Main.getInstance().getConfig().getLocation("minions." + key);

            if (minionLocation.getBlockX() == location.getBlockX() &&
                    minionLocation.getBlockY() == location.getBlockY() &&
                    minionLocation.getBlockZ() == location.getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    public MinionType getMinionType(Location location) {
        for (String key : Main.getInstance().getConfig().getConfigurationSection("minions").getKeys(false)) {
            Location minionLocation = Main.getInstance().getConfig().getLocation("minions." + key);

            if (minionLocation.getBlockX() == location.getBlockX() &&
                    minionLocation.getBlockY() == location.getBlockY() &&
                    minionLocation.getBlockZ() == location.getBlockZ()) {
                return MinionType.valueOf(key);
            }
        }
        return null;
    }
}
