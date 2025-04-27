package org.DBsGameplay.oreBreaker.listeners;

import org.DBsGameplay.oreBreaker.Main;
import org.DBsGameplay.oreBreaker.utils.PickaxeStats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class Events implements Listener {

    private final Material[] materiallist = {Material.COAL_ORE,Material.IRON_ORE,Material.COPPER_ORE,Material.GOLD_ORE,Material.REDSTONE_ORE,Material.EMERALD_ORE,Material.LAPIS_ORE,Material.DIAMOND_ORE};
    private final PickaxeStats pickaxeStats = new PickaxeStats();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();

        //spawn
        try {
            Location spawnLocation = Main.getInstance().getConfig().getLocation("spawn");
            player.teleport(spawnLocation);
        }catch (Exception e) {
            player.teleport(player.getWorld().getSpawnLocation());
        }

        //give player pickaxe
        if (!player.getInventory().contains(Material.NETHERITE_PICKAXE)) {
            player.getInventory().addItem(getPickaxe(player));
        }

        //attribute ändern
        if (pickaxeStats.getSpeed(player) == 0) {
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
        }else {
            if ((0.1 + (pickaxeStats.getSpeed(player)*0.001)) >= 0.5) {
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
            }
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1 + (pickaxeStats.getSpeed(player)*0.001));
        }

        if (pickaxeStats.getEfficiency(player) == 0) {
            player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(1.0);
        }else {
            if ((0.1 + (pickaxeStats.getSpeed(player)*0.5)) >= 10) {
                player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(10);
            }
            player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(1.0 + (pickaxeStats.getSpeed(player)*0.5));
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();

        //open pickaxemenu on condition
        if (event.getItem() == null || event.getItem().getType() == null) return;
        if (!(event.getAction().isRightClick() && event.getItem().getType() == Material.NETHERITE_PICKAXE)) return;

        openPickaxeMenu(player);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event){
        Player player = event.getPlayer();

        for (Material material : materiallist) {
            if (material == event.getBlock().getType()) {
                event.setDropItems(false);

                int abbauAnzahl = pickaxeStats.getBlockabbau(player);
                int brokenBlocks = 1;
                Random random = new Random();
                Block centerBlock = event.getBlock();
                Location centerLocation = centerBlock.getLocation();

                // Schleife durch die Blöcke im Radius
                for (int i = 0; i < abbauAnzahl; i++) {
                    // Zufällige Verschiebung in x-, y- und z-Richtung
                    int offsetX = random.nextInt(3) - 1; // -1, 0 oder 1
                    int offsetY = random.nextInt(3) - 1;
                    int offsetZ = random.nextInt(3) - 1;

                    Location offsetLocation = centerLocation.clone().add(offsetX, offsetY, offsetZ);
                    Block offsetBlock = offsetLocation.getBlock();

                    // Überprüfen, ob der Block abbaubar ist und nicht der ursprüngliche Block ist
                    if (offsetBlock.getType() != Material.AIR && !offsetBlock.equals(centerBlock)) {
                        offsetBlock.breakNaturally(); // Block abbauen
                        brokenBlocks++;
                    }
                }

                pickaxeStats.setBlocksMined(player, pickaxeStats.getBlocksMined(player)+brokenBlocks);

                int multiplicator = pickaxeStats.getAbbaurate(player) * brokenBlocks; //Mehr fürs abbauen bekommen

                try {
                    Main.getInstance().getConfig().set("balance." + player.getUniqueId() + "."  + event.getBlock().getType().name().replace("_ORE", ""), Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "."  +  event.getBlock().getType().name().replace("_ORE", "")) + multiplicator);
                    Main.getInstance().saveConfig();
                }catch  (Exception e) {
                    Main.getInstance().getConfig().set("balance." + player.getUniqueId() + "."  + event.getBlock().getType().name().replace("_ORE", ""), multiplicator); //catch falls ballance nicht vorhanden
                    Main.getInstance().saveConfig();
                }

                return;
            }
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event){
        if (event.getView().getTitle().equals("Menü")){
            event.setCancelled(true);

            if (event.getCurrentItem() == null) return;

            if (event.getCurrentItem().getType() == Material.ANVIL){
                Player player = (Player) event.getWhoClicked();

                if (canAffordUpgrade(player)){
                    pickaxeStats.setLevel(player, pickaxeStats.getLevel(player)+1);

                    if (isEven(pickaxeStats.getLevel(player))) {
                        pickaxeStats.setAbbaurate(player, pickaxeStats.getAbbaurate(player)+1);
                    }
                    if (String.valueOf(pickaxeStats.getLevel(player)).endsWith("5")) {// Bei jedem 5ten
                        pickaxeStats.setEfficiency(player, pickaxeStats.getEfficiency(player)+1);
                    }
                    if (String.valueOf(pickaxeStats.getLevel(player)).endsWith("0")) {// Bei jedem 10ten
                        pickaxeStats.setSpeed(player, pickaxeStats.getSpeed(player)+1);
                    }

                    if (pickaxeStats.getLevel(player) == 100){
                        pickaxeStats.setLevel(player, 0);
                        pickaxeStats.setPrestigeLevel(player, pickaxeStats.getPrestigeLevel(player)+1);

                        if (pickaxeStats.getPrestigeLevel(player) % 4 == 0) {
                            pickaxeStats.setBlockabbau(player, pickaxeStats.getBlockabbau(player)+1);
                        }

                        Main.getInstance().getConfig().set("balance." + player.getUniqueId(), null); //materialien zurücksetzen
                        Main.getInstance().saveConfig();
                    }

                    player.getInventory().clear();
                    player.sendMessage("Du hast erfolgreich geupgraded.");
                    player.getInventory().addItem(getPickaxe(player));
                    openPickaxeMenu(player);
                } else {
                    player.sendMessage("Du hast nicht genug Materialien.");
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        Player p = (Player) event.getPlayer();

        p.getInventory().remove(Material.NETHERITE_PICKAXE);
        p.getInventory().addItem(getPickaxe(p));
    }

    private void openPickaxeMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27,"Menü");
        inventory.setItem(0, createDisplayOre(Material.COAL_ORE,"COAL", player));
        inventory.setItem(2, createDisplayOre(Material.IRON_ORE,"IRON", player));
        inventory.setItem(6, createDisplayOre(Material.COPPER_ORE,"COPPER", player));
        inventory.setItem(8, createDisplayOre(Material.GOLD_ORE,"GOLD", player));
        inventory.setItem(18, createDisplayOre(Material.REDSTONE_ORE,"REDSTONE", player));
        inventory.setItem(20, createDisplayOre(Material.EMERALD_ORE,"EMERALD", player));
        inventory.setItem(24, createDisplayOre(Material.LAPIS_ORE,"LAPIS", player));
        inventory.setItem(26, createDisplayOre(Material.DIAMOND_ORE,"DIAMOND", player));

        ItemStack upgrade = new ItemStack(Material.ANVIL);
        ItemMeta upgrademeta = upgrade.getItemMeta();

        upgrademeta.setDisplayName("§d----------Upgrades-------");
        List<String> lore = new ArrayList<>();
        if (pickaxeStats.getLevel(player) == 99) {
            lore.add("§4Prestige-Level: §b" + pickaxeStats.getPrestigeLevel(player) + "§a +1");
        } else {
            lore.add("§4Prestige-Level: §b" + pickaxeStats.getPrestigeLevel(player));
        }
        lore.add("§cLevel: §b" + pickaxeStats.getLevel(player) + "§a +1");
        if (isEven(pickaxeStats.getLevel(player)+1)) { //bei 2,4,6,8,10 jede 2te
            lore.add("§6Abbaurate: §b" + pickaxeStats.getAbbaurate(player) + "§a +1");
        } else {
            lore.add("§6Abbaurate: §b" + pickaxeStats.getAbbaurate(player));
        }
        if (String.valueOf(pickaxeStats.getLevel(player)).endsWith("5")) {// Bei jedem 5ten
            lore.add("§dEffizienz §a +1");
        }
        if (String.valueOf(pickaxeStats.getLevel(player)).endsWith("0") && pickaxeStats.getLevel(player) != 0) {// Bei jedem 5ten
            lore.add("§dGeschwindigkeit §a +1");
        }
        if (pickaxeStats.getPrestigeLevel(player) % 4 == 0 && pickaxeStats.getLevel(player) == 99) {
            lore.add("§dBlockabbau §a +1");
        }
        lore.add("§7-------------------------------");

        // Calculate stage based on prestige level
        int stage = (int) Math.floor(pickaxeStats.getPrestigeLevel(player) / 8);

        // Calculate materials needed based on prestige level
        int materialIndex;
        if (pickaxeStats.getPrestigeLevel(player) < 8) {
            // Prestige 0-7: increasing materials (1-8)
            materialIndex = pickaxeStats.getPrestigeLevel(player) + 1;
        } else {
            // For Prestige 8+: cycle through materials (1-8) based on remainder
            materialIndex = (pickaxeStats.getPrestigeLevel(player) % 8) + 1;
        }

        HashMap<String, Integer> upgradeCostMap = new LinkedHashMap<>(); // LinkedHashMap to maintain insertion order
        for (int i = 0; i < materialIndex; i++) {
            Material material = materiallist[i];
            String sMaterial = material.name().replace("_ORE", "") + "_LEVEL_" + stage;

            int cost = (80 - (i * 5)) + (pickaxeStats.getLevel(player)*7);

            upgradeCostMap.put(sMaterial, cost);
        }

        lore.add("§7Upgrade Kosten: §b");

        //Materialien anzeigen
        for (Map.Entry<String, Integer> entry : upgradeCostMap.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            if (key.endsWith("_0")) {
                lore.add("§6" + key.replace("_LEVEL_0", "") + ": §b" + value);
            } else {
                lore.add("§6" + key + ": §b" + value);
            }
        }

        upgrademeta.setLore(lore);
        upgrade.setItemMeta(upgrademeta);
        inventory.setItem(13,upgrade);

        //rest mit glas füllen
        for (int i = 0; i != inventory.getSize(); i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i,ItemStack.of(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(inventory);
    }

    private boolean canAffordUpgrade(Player player) {
        // Calculate stage based on prestige level
        int stage = (int) Math.floor(pickaxeStats.getPrestigeLevel(player) / 8);

        // Calculate materials needed based on prestige level
        int materialIndex;
        if (pickaxeStats.getPrestigeLevel(player) < 8) {
            // Prestige 0-7: increasing materials (1-8)
            materialIndex = pickaxeStats.getPrestigeLevel(player) + 1;
        } else {
            // For Prestige 8+: cycle through materials (1-8) based on remainder
            materialIndex = (pickaxeStats.getPrestigeLevel(player) % 8) + 1;
        }

        HashMap<String, Integer> upgradeCostMap = new HashMap<>();
        for (int i = 0; i < materialIndex; i++) {
            Material material = materiallist[i];
            String sMaterial = material.name().replace("_ORE", "") + "_LEVEL_" + stage;

            int cost = (80 - (i * 5)) + (pickaxeStats.getLevel(player) * 7);
            upgradeCostMap.put(sMaterial, cost);
        }

        //Checken ob er es sich leisten kann
        for (String s : upgradeCostMap.keySet()) {
            int balance;

            if (s.endsWith("_0")) {
                balance = Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "." + s.replace("_LEVEL_0", ""));
            } else {
                balance = Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "." + s);
            }

            int price = upgradeCostMap.get(s);

            if (balance < price) { //returnt false falls er ihrgendwas nicht zahlen kann
                return false;
            }
        }

        //beträge abziehen
        for (String s : upgradeCostMap.keySet()) {
            int balance;
            int price = upgradeCostMap.get(s);

            if (s.endsWith("_0")) {
                balance = Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "." + s.replace("_LEVEL_0", ""));
                Main.getInstance().getConfig().set("balance." + player.getUniqueId() + "." + s.replace("_LEVEL_0", ""), balance-price);
                Main.getInstance().saveConfig();
            } else {
                balance = Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "." + s);
                Main.getInstance().getConfig().set("balance." + player.getUniqueId() + "." + s, balance-price);
                Main.getInstance().saveConfig();
            }
        }

        return true;
    }

    private ItemStack getPickaxe(Player player) {
        ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta pickaxemeta = pickaxe.getItemMeta();

        pickaxemeta.setDisplayName("§6Pickaxe §7[Lv. " + pickaxeStats.getLevel(player) + "]");

        List<String> lore = new ArrayList<>();
        lore.add("§a||Leveling:");
        lore.add("§a||§4Prestige-Level: §b" + pickaxeStats.getPrestigeLevel(player));
        lore.add("§a||§cLevel: §b" + pickaxeStats.getLevel(player));
        lore.add("§a||§eAbgebaute Blöcke: §b" + pickaxeStats.getBlocksMined(player));
        lore.add("");
        lore.add("§c||Entchantments:");

        lore.add("§c||§6Abbaurate: §b" + pickaxeStats.getAbbaurate(player) + " §6pro Block");
        lore.add("§c||§6Blockabbau: §b" + pickaxeStats.getBlockabbau(player) + " §6im Radius");
        lore.add("§c||§6Effizienz: §b" + pickaxeStats.getEfficiency(player));

        pickaxemeta.setLore(lore);
        pickaxemeta.setUnbreakable(true);
        pickaxe.setItemMeta(pickaxemeta);

        return pickaxe;
    }

    private ItemStack createDisplayOre(Material material,String name, Player player){
        ItemStack kohle_tasche = new ItemStack(material);
        ItemMeta kohle_taschemeta = kohle_tasche.getItemMeta();

        kohle_taschemeta.setDisplayName(name + ": §b" + Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "."  + name));
        List<String> lore = new ArrayList<>();

        for (int i = 1; i != 16; i++) {
            loadLore(name, player.getUniqueId(), lore, i);
        }

        kohle_taschemeta.setLore(lore);
        kohle_tasche.setItemMeta(kohle_taschemeta);

        return kohle_tasche;
    }

    private void loadLore(String name, UUID uuid, List<String> lore, int num) {
        int num2 = num+15;
        int amount = Main.getInstance().getConfig().getInt("balance." + uuid + "."  + name  + "_LEVEL_" + num);
        int amount2 = Main.getInstance().getConfig().getInt("balance." + uuid + "."  + name  + "_LEVEL_" + num2);

        if (num < 10) {
            lore.add("§aLvl.  " + num + ": §b" + amount + "     " + "§aLv. " + num2 + ": §b" + amount2);
        } else {
            lore.add("§aLv. " + num + ": §b" + amount + "     " + "§aLv. " + num2 + ": §b" + amount2);
        }
    }

    private boolean isEven(int zahl) {
        return zahl % 2 == 0;
    }
}