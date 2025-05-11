package org.DBsGameplay.oreBreaker.gui;

import org.DBsGameplay.oreBreaker.utils.MinionStats;
import org.DBsGameplay.oreBreaker.utils.MinionType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MinionGUI implements InventoryHolder {

    private final Inventory inventory = org.bukkit.Bukkit.createInventory(this, 9*6, "Minion");

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    MinionStats minionStats = new MinionStats();

    public void openMinionGUI(Player player, MinionType minionType) {
        inventory.clear();

        // Glasränder setzen
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta glassMeta = glass.getItemMeta();
                glassMeta.setDisplayName(" ");
                glass.setItemMeta(glassMeta);
                inventory.setItem(i, glass);
            }
        }

        // Info Star (Minion Status)
        ItemStack infoItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("§6» §eMinion Information");
        infoMeta.setLore(List.of(
                "§7",
                "§7Typ: §6" + minionType.name(),
                "§7Freigeschaltete Blöcke: §a" + minionStats.getUnlockedBlocks(player, minionType) + "§8/§c28",
                "§7Mining Level: §a" + minionStats.getMiningLevel(player, minionType) + "§8/§c1000"
        ));
        infoItem.setItemMeta(infoMeta);
        inventory.setItem(4, infoItem);

        int unlockedBlocks = minionStats.getUnlockedBlocks(player, minionType);

        // Upgrade Buttons
        ItemStack unlockItem = new ItemStack(Material.CHEST_MINECART);
        ItemMeta unlockMeta = unlockItem.getItemMeta();
        unlockMeta.setDisplayName("§6» Mehr Erze freischalten");
        unlockMeta.setItemName("unlock_" + minionType.name());
        unlockMeta.setLore(List.of(
                "§7",
                "§7Aktuelle Kapazität: §a" + unlockedBlocks,
                "§7Kosten: §6" + (4500 * unlockedBlocks) + " " + minionType.name(),
                "§7",
                "§e»Klicke zum Freischalten!"
        ));
        unlockItem.setItemMeta(unlockMeta);
        inventory.setItem(48, unlockItem);

        ItemStack miningItem = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta miningMeta = miningItem.getItemMeta();
        miningMeta.setDisplayName("§6» Effizienz verbessern");
        miningMeta.setItemName("mining_" + minionType.name());
        miningMeta.setLore(List.of(
                "§7",
                "§7Aktuelles Level: §a" + minionStats.getMiningLevel(player, minionType),
                "§7Kosten: §6" + (1500 * minionStats.getMiningLevel(player, minionType)) + " " + minionType.name(),
                "§7",
                "§e»Klicke zum Verbessern!"
        ));
        miningItem.setItemMeta(miningMeta);
        inventory.setItem(50, miningItem);

        // Freigeschaltete Erze anzeigen
        for (int i = 0; i < unlockedBlocks && i < 28; i++) {
            ItemStack ore = new ItemStack(minionType.getMaterial());
            ItemMeta oreMeta = ore.getItemMeta();
            oreMeta.setDisplayName("§a✔ §7Freigeschaltet");
            oreMeta.setItemName(String.valueOf(i));
            ore.setItemMeta(oreMeta);
            inventory.addItem(ore);
        }

        player.openInventory(inventory);
    }

    public void openBuyMinionGUI(Player player, MinionType minionType) {
        inventory.clear();

        // Hintergrundfüllung
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta glassMeta = glass.getItemMeta();
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
            inventory.setItem(i, glass);
        }

        ItemStack buyItem = new ItemStack(Material.EMERALD);
        ItemMeta buyMeta = buyItem.getItemMeta();
        buyMeta.setDisplayName("§6» " + minionType.name() + " Minion kaufen");
        buyMeta.setItemName("buy_minion_" + minionType.name());
        buyMeta.setLore(List.of(
                "§7",
                "§7Benötigtes Prestige: §6" + minionType.getNeededPrestige(),
                "§7Kosten: §61.000.000 " + minionType.name(),
                "§7",
                "§e»Klicke zum Kaufen!"
        ));
        buyItem.setItemMeta(buyMeta);
        inventory.setItem(22, buyItem);

        player.openInventory(inventory);
    }
}
