package org.DBsGameplay.oreBreaker.listeners;

import org.DBsGameplay.oreBreaker.Main;
import org.DBsGameplay.oreBreaker.gui.MinionGUI;
import org.DBsGameplay.oreBreaker.utils.MinionStats;
import org.DBsGameplay.oreBreaker.utils.MinionType;
import org.DBsGameplay.oreBreaker.utils.MinionsManager;
import org.DBsGameplay.oreBreaker.utils.PickaxeStats;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MinionListener implements Listener {

    final MinionGUI minionGUI = new MinionGUI();
    final MinionStats minionStats = new MinionStats();
    final PickaxeStats pickaxeStats = new PickaxeStats();
    final MinionsManager minionsManager = new MinionsManager();

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() == Material.CHEST && minionsManager.isMinion(event.getClickedBlock().getLocation())) {
            Player player = event.getPlayer();

            event.setCancelled(true);

            if (minionStats.isMinionUnlocked(player, minionsManager.getMinionType(event.getClickedBlock().getLocation()))) {
                minionGUI.openMinionGUI(player, minionsManager.getMinionType(event.getClickedBlock().getLocation()));

                for (String key : Main.getInstance().getConfig().getConfigurationSection("minionstats." + player.getUniqueId()).getKeys(false)) {
                    MinionType type = MinionType.valueOf(key);

                    int minedAmount = Main.getInstance().getConfig().getInt("minionsmined." + player.getUniqueId() + "." + type.name());

                    if (minedAmount > 0) {

                        String balancePath = "balance." + player.getUniqueId() + "." + type.name().toUpperCase() + "_LEVEL_0";
                        int currentBalance = Main.getInstance().getConfig().getInt(balancePath, 0);
                        Main.getInstance().getConfig().set(balancePath, currentBalance + minedAmount);

                        Main.getInstance().getConfig().set("minionsmined." + player.getUniqueId() + "." + type.name(), 0);
                        Main.getInstance().saveConfig();

                        // Sende Nachricht
                        player.sendMessage("§aDein " + type.name() + " Minion hat §6" + minedAmount + " " + type.name() + " §agesammelt!");
                    }
                }
            }else {
                minionGUI.openBuyMinionGUI(player, minionsManager.getMinionType(event.getClickedBlock().getLocation()));
            }
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof MinionGUI)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null) return;
        if (event.isRightClick()) return;

        if (event.getCurrentItem().getItemMeta().getItemName().startsWith("buy_minion_")) {
            handleBuyMinion(player, event);
        }else if (event.getCurrentItem().getItemMeta().getItemName().startsWith("unlock_")) {
            handleUpgradeUnlock(player, event);
        }else if (event.getCurrentItem().getItemMeta().getItemName().startsWith("mining_")) {
            handleEffizienzUnlock(player, event);
        }
    }

    private void handleBuyMinion(Player player, InventoryClickEvent event) {
        MinionType minionType = MinionType.valueOf(event.getCurrentItem().getItemMeta().getItemName().replace("buy_minion_", ""));

        if (!(pickaxeStats.getPrestigeLevel(player) >= minionType.getNeededPrestige())) {
            player.sendMessage("§cDu hast nicht genug Prestige Level! Du brauchst noch: " + (minionType.getNeededPrestige() - pickaxeStats.getPrestigeLevel(player)));
            return;
        }

        int cost = 100000;
        int balance = Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "." + minionType.name().toUpperCase() + "_LEVEL_0", 0);

        if (balance >= cost) {
            Main.getInstance().getConfig().set("balance." + player.getUniqueId() + "." + minionType.name().toUpperCase() + "_LEVEL_0", balance - cost);
            Main.getInstance().saveConfig();

            minionStats.setMinionUnlocked(player, minionType, true);
            player.sendMessage("§aDu hast erfolgreich den " + minionType.name() + " freigeschaltet!");
            player.closeInventory();
        } else {
            player.sendMessage("§cDu hast nicht genug " + minionType.name() + "! Du brauchst noch: " + (cost - balance));
        }
    }

    private void handleUpgradeUnlock(Player player, InventoryClickEvent event) {
        MinionType minionType = MinionType.valueOf(event.getCurrentItem().getItemMeta().getItemName().replace("unlock_", ""));

        if (minionStats.getUnlockedBlocks(player, minionType) >= 28) {
            player.sendMessage("§cDu hast bereits das maximale Level erreicht!");
            return;
        }

        int cost = 4500 * minionStats.getUnlockedBlocks(player, minionType);
        int balance = Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "." + minionType.name().toUpperCase() + "_LEVEL_0", 0);

        if (balance >= cost) {
            Main.getInstance().getConfig().set("balance." + player.getUniqueId() + "." + minionType.name().toUpperCase() + "_LEVEL_0", balance - cost);
            Main.getInstance().saveConfig();

            minionStats.setUnlockedBlocks(player, minionType, minionStats.getUnlockedBlocks(player, minionType) + 1);
            player.sendMessage("§aDu hast erfolgreich den " + minionType.name() + " verbessert!");
            minionGUI.openMinionGUI(player, minionType);
        } else {
            player.sendMessage("§cDu hast nicht genug " + minionType.name() + "! Du brauchst noch: " + (cost - balance));
        }
    }

    private void handleEffizienzUnlock(Player player, InventoryClickEvent event) {
        MinionType minionType = MinionType.valueOf(event.getCurrentItem().getItemMeta().getItemName().replace("mining_", ""));

        if (minionStats.getMiningLevel(player, minionType) >= 1000) {
            player.sendMessage("§cDu hast bereits das maximale Level erreicht!");
            return;
        }

        int cost = 1500 * minionStats.getMiningLevel(player, minionType);
        int balance = Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "." + minionType.name().toUpperCase() + "_LEVEL_0", 0);

        if (balance >= cost) {
            Main.getInstance().getConfig().set("balance." + player.getUniqueId() + "." + minionType.name().toUpperCase() + "_LEVEL_0", balance - cost);
            Main.getInstance().saveConfig();

            minionStats.setMiningLevel(player, minionType, minionStats.getMiningLevel(player, minionType) + 1);
            player.sendMessage("§aDu hast erfolgreich den " + minionType.name() + " verbessert!");
            minionGUI.openMinionGUI(player, minionType);
        } else {
            player.sendMessage("§cDu hast nicht genug " + minionType.name() + "! Du brauchst noch: " + (cost - balance));
        }
    }
}