package org.DBsGameplay.oreBreaker.utils;

import org.bukkit.Material;

public enum MinionType {
    COAL(Material.COAL_ORE,9),
    IRON(Material.IRON_ORE,10),
    GOLD(Material.GOLD_ORE,12),
    COPPER(Material.COPPER_ORE,11),
    REDSTONE(Material.REDSTONE,13),
    EMERALD(Material.EMERALD,14),
    LAPIS(Material.LAPIS_ORE,15),
    DIAMOND(Material.DIAMOND,16);

    private final Material material;
    private final int neededPrestige;

    MinionType(Material material, int neededPrestige) {
        this.material = material;
        this.neededPrestige = neededPrestige;
    }

    public Material getMaterial() {
        return material;
    }

    public int getNeededPrestige() {
        return neededPrestige;
    }
}
