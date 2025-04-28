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

    private static final String MENU_TITLE = "Menü";
    private static final int INVENTORY_SIZE = 27;
    private static final double BASE_MOVEMENT_SPEED = 0.1;
    private static final double MAX_MOVEMENT_SPEED = 0.5;
    private static final double BASE_BREAK_SPEED = 1.0;
    private static final double MAX_BREAK_SPEED = 10.0;
    private static final int BASE_UPGRADE_COST = 80;
    private static final int LEVEL_COST_MULTIPLIER = 7;
    private static final int MAX_LEVEL = 100;
    private static final int MATERIALS_PER_STAGE = 8;

    private final Material[] materialList = {
            Material.COAL_ORE, Material.IRON_ORE, Material.COPPER_ORE, Material.GOLD_ORE,
            Material.REDSTONE_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE, Material.DIAMOND_ORE
    };
    private final PickaxeStats pickaxeStats = new PickaxeStats();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        teleportToSpawn(player);
        giveInitialPickaxe(player);
        updatePlayerAttributes(player);

        Main.getInstance().getConfig().set("players." + player.getUniqueId() + ".name", player.getName());
        Main.getInstance().saveConfig();
    }

    private void teleportToSpawn(Player player) {
        try {
            Location spawnLocation = Main.getInstance().getConfig().getLocation("spawn");
            player.teleport(spawnLocation != null ? spawnLocation : player.getWorld().getSpawnLocation());
        } catch (Exception e) {
            player.teleport(player.getWorld().getSpawnLocation());
        }
    }

    private void giveInitialPickaxe(Player player) {
        if (!player.getInventory().contains(Material.NETHERITE_PICKAXE)) {
            player.getInventory().addItem(getPickaxe(player));
        }
    }

    private void updatePlayerAttributes(Player player) {
        updateMovementSpeed(player);
        updateBreakSpeed(player);
    }

    private void updateMovementSpeed(Player player) {
        int speedLevel = pickaxeStats.getSpeed(player);
        double newSpeed = speedLevel == 0 ? BASE_MOVEMENT_SPEED :
                Math.min(BASE_MOVEMENT_SPEED + (speedLevel * 0.001), MAX_MOVEMENT_SPEED);
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(newSpeed);
    }

    private void updateBreakSpeed(Player player) {
        int efficiencyLevel = pickaxeStats.getEfficiency(player);
        double newSpeed = efficiencyLevel == 0 ? BASE_BREAK_SPEED :
                Math.min(BASE_BREAK_SPEED + (pickaxeStats.getSpeed(player) * 0.5), MAX_BREAK_SPEED);
        player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(newSpeed);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!isValidPickaxeInteraction(event)) return;
        openPickaxeMenu(event.getPlayer());
    }

    private boolean isValidPickaxeInteraction(PlayerInteractEvent event) {
        return event.getItem() != null &&
                event.getItem().getType() == Material.NETHERITE_PICKAXE &&
                event.getAction().isRightClick();
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (!Arrays.asList(materialList).contains(event.getBlock().getType())) {
            event.setCancelled(true);
            return;
        }

        handleOreBreak(event);
    }

    private void handleOreBreak(BlockBreakEvent event) {
        event.setDropItems(false);
        Player player = event.getPlayer();
        int brokenBlocks = breakSurroundingBlocks(event.getBlock(), player);
        updatePlayerStats(player, event.getBlock(), brokenBlocks);
    }

    private int breakSurroundingBlocks(Block centerBlock, Player player) {
        int abbauAnzahl = pickaxeStats.getBlockabbau(player);
        int brokenBlocks = 1;
        Random random = new Random();
        Location centerLocation = centerBlock.getLocation();

        for (int i = 0; i < abbauAnzahl; i++) {
            Location offsetLocation = centerLocation.clone().add(
                    random.nextInt(3) - 1,
                    random.nextInt(3) - 1,
                    random.nextInt(3) - 1
            );

            if (breakBlockIfValid(offsetLocation.getBlock(), centerBlock)) {
                brokenBlocks++;
            }
        }

        return brokenBlocks;
    }

    private boolean breakBlockIfValid(Block block, Block centerBlock) {
        if (block.equals(centerBlock) || block.getType() == Material.AIR) {
            return false;
        }

        if (Arrays.asList(materialList).contains(block.getType())) {
            block.breakNaturally();
            return true;
        }

        return false;
    }

    private void updatePlayerStats(Player player, Block block, int brokenBlocks) {
        pickaxeStats.setBlocksMined(player, pickaxeStats.getBlocksMined(player) + brokenBlocks);
        updatePlayerBalance(player, block, brokenBlocks);
    }

    private void updatePlayerBalance(Player player, Block block, int brokenBlocks) {
        String materialKey = block.getType().name().replace("_ORE", "");
        int multiplicator = pickaxeStats.getAbbaurate(player) + brokenBlocks;
        int stage = (int) Math.floor(pickaxeStats.getPrestigeLevel(player) / MATERIALS_PER_STAGE);

        // Normales Material hinzufügen
        addMaterialToBalance(player, materialKey, "", multiplicator);

        // 15% Chance auf höherstufiges Material
        if (Math.random() < 0.15) { // 15% Chance
            int materialIndex = calculateMaterialIndex(player);
            int currentMaterialIndex = getMaterialIndex(block.getType());

            // Überprüfe ob es ein höherwertiges Material gibt und ob es freigeschaltet ist
            if (currentMaterialIndex < materialIndex - 1 && currentMaterialIndex < materialList.length - 1) {
                // Nächstes Material in der Liste
                Material nextMaterial = materialList[currentMaterialIndex + 1];
                String nextMaterialKey = nextMaterial.name().replace("_ORE", "");

                // Füge höherwertiges Material hinzu
                addMaterialToBalance(player, nextMaterialKey, "_LEVEL_" + stage, 1);
                player.sendMessage("§a✦ Bonus: §e+1 " + nextMaterialKey);
            }
        }
    }

    private void addMaterialToBalance(Player player, String materialKey, String suffix, int amount) {
        String balancePath = "balance." + player.getUniqueId() + "." + materialKey + suffix;
        Main.getInstance().getConfig().set(balancePath,
                Main.getInstance().getConfig().getInt(balancePath, 0) + amount);
        Main.getInstance().saveConfig();
    }

    private int getMaterialIndex(Material material) {
        for (int i = 0; i < materialList.length; i++) {
            if (materialList[i] == material) {
                return i;
            }
        }
        return -1;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(MENU_TITLE)) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.ANVIL) return;

        Player player = (Player) event.getWhoClicked();
        handleUpgradeClick(player);
    }

    private void handleUpgradeClick(Player player) {
        if (!canAffordUpgrade(player)) {
            player.sendMessage("Du hast nicht genug Materialien.");
            return;
        }

        updatePlayerLevels(player);
        resetPlayerInventory(player);
        player.sendMessage("Du hast erfolgreich geupgraded.");
    }

    private void updatePlayerLevels(Player player) {
        pickaxeStats.setLevel(player, pickaxeStats.getLevel(player) + 1);
        int currentLevel = pickaxeStats.getLevel(player);

        if (isEven(currentLevel)) {
            pickaxeStats.setAbbaurate(player, pickaxeStats.getAbbaurate(player) + 1);
        }
        if (String.valueOf(currentLevel).endsWith("5") && pickaxeStats.getEfficiency(player) != 150) {
            pickaxeStats.setEfficiency(player, pickaxeStats.getEfficiency(player) + 1);
        }
        if (String.valueOf(currentLevel).endsWith("0") && pickaxeStats.getSpeed(player) != 5) {
            pickaxeStats.setSpeed(player, pickaxeStats.getSpeed(player) + 1);
        }
        if (pickaxeStats.getPrestigeLevel(player) % 4 == 0 && pickaxeStats.getBlockabbau(player) != 50) {
            pickaxeStats.setBlockabbau(player, pickaxeStats.getBlockabbau(player) + 1);
        }

        handlePrestigeLevelUp(player);
    }

    private void handlePrestigeLevelUp(Player player) {
        if (pickaxeStats.getLevel(player) != MAX_LEVEL) return;

        pickaxeStats.setLevel(player, 0);
        pickaxeStats.setPrestigeLevel(player, pickaxeStats.getPrestigeLevel(player) + 1);

        Main.getInstance().getConfig().set("balance." + player.getUniqueId(), null);
        Main.getInstance().saveConfig();
    }

    private void resetPlayerInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().addItem(getPickaxe(player));
        openPickaxeMenu(player);
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        player.getInventory().remove(Material.NETHERITE_PICKAXE);
        player.getInventory().addItem(getPickaxe(player));
    }

    private ItemStack getPickaxe(Player player) {
        ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();

        meta.setDisplayName("§6Legendary Pickaxe §a[Lv. " + pickaxeStats.getLevel(player) + "]");
        meta.setLore(createPickaxeLore(player));
        meta.setUnbreakable(true);
        pickaxe.setItemMeta(meta);

        return pickaxe;
    }

    private List<String> createPickaxeLore(Player player) {
        List<String> lore = new ArrayList<>();

        // Leveling Stats
        lore.add("§5§nLeveling-Stats:");
        lore.add("§e- Pickaxe Level: §b" + pickaxeStats.getLevel(player) + "/100");
        lore.add("§c- Prestige Level: §b" + pickaxeStats.getPrestigeLevel(player) + "/240");
        lore.add("§9- Abgebaute Blöcke: §b" + pickaxeStats.getBlocksMined(player));
        lore.add("");

        // Enchantments Stats
        lore.add("§5§nEntchantments-Stat:");
        lore.add("§e- Abbaurate: §b" + pickaxeStats.getAbbaurate(player) + " §apro Block");
        lore.add("§d- Blockabbau §b" + pickaxeStats.getBlockabbau(player) + "/50 §aim Radius");
        lore.add("§4- Blockabbau Effizienz: §b" + pickaxeStats.getSpeed(player));
        lore.add("§9- Geschwindigkeits Level: §b" + pickaxeStats.getSpeed(player));
        lore.add("§a- Jackhammer eine Reihe auslöse");
        lore.add("§aChance §b0.01% §avon §b35% Maximal");

        return lore;
    }

    private ItemStack createDisplayOre(Material material, String name, Player player) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(name + ": §b" + Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "." + name));
        List<String> lore = new ArrayList<>();

        for (int i = 1; i < 16; i++) {
            loadLore(name, player.getUniqueId(), lore, i);
        }

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private void loadLore(String name, UUID uuid, List<String> lore, int num) {
        int num2 = num + 15;
        int amount = Main.getInstance().getConfig().getInt("balance." + uuid + "." + name + "_LEVEL_" + num);
        int amount2 = Main.getInstance().getConfig().getInt("balance." + uuid + "." + name + "_LEVEL_" + num2);

        String format = num < 10 ? "§aLvl.  %d: §b%d     §aLv. %d: §b%d" : "§aLv. %d: §b%d     §aLv. %d: §b%d";
        lore.add(String.format(format, num, amount, num2, amount2));
    }

    private boolean isEven(int number) {
        return number % 2 == 0;
    }

    private void openPickaxeMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, MENU_TITLE);

        // Set ore displays
        inventory.setItem(0, createDisplayOre(Material.COAL_ORE, "COAL", player));
        inventory.setItem(2, createDisplayOre(Material.IRON_ORE, "IRON", player));
        inventory.setItem(6, createDisplayOre(Material.COPPER_ORE, "COPPER", player));
        inventory.setItem(8, createDisplayOre(Material.GOLD_ORE, "GOLD", player));
        inventory.setItem(18, createDisplayOre(Material.REDSTONE_ORE, "REDSTONE", player));
        inventory.setItem(20, createDisplayOre(Material.EMERALD_ORE, "EMERALD", player));
        inventory.setItem(24, createDisplayOre(Material.LAPIS_ORE, "LAPIS", player));
        inventory.setItem(26, createDisplayOre(Material.DIAMOND_ORE, "DIAMOND", player));

        // Create upgrade anvil
        ItemStack upgrade = createUpgradeAnvil(player);
        inventory.setItem(13, upgrade);

        // Fill empty slots with glass panes
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, ItemStack.of(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(inventory);
    }

    private ItemStack createUpgradeAnvil(Player player) {
        ItemStack upgrade = new ItemStack(Material.ANVIL);
        ItemMeta meta = upgrade.getItemMeta();
        List<String> lore = new ArrayList<>();

        meta.setDisplayName("§d----------Upgrades-------");

        // Add prestige and level information
        if (pickaxeStats.getLevel(player) == 99) {
            lore.add("§4Prestige-Level: §b" + pickaxeStats.getPrestigeLevel(player) + "§a +1");
        } else {
            lore.add("§4Prestige-Level: §b" + pickaxeStats.getPrestigeLevel(player));
        }
        lore.add("§cLevel: §b" + pickaxeStats.getLevel(player) + "§a +1");

        // Add upgrade information
        addUpgradeInfo(player, lore);
        lore.add("§7-------------------------------");

        // Add material costs
        addMaterialCosts(player, lore);

        meta.setLore(lore);
        upgrade.setItemMeta(meta);
        return upgrade;
    }

    private boolean canAffordUpgrade(Player player) {
        int stage = (int) Math.floor(pickaxeStats.getPrestigeLevel(player) / MATERIALS_PER_STAGE);
        int materialIndex = calculateMaterialIndex(player);
        Map<String, Integer> costs = calculateUpgradeCosts(player, stage, materialIndex);

        // Check if player can afford all materials
        if (!checkMaterialsAvailable(player, costs)) {
            return false;
        }

        // Deduct materials if player can afford
        deductMaterials(player, costs);
        return true;
    }

    private int calculateMaterialIndex(Player player) {
        int prestigeLevel = pickaxeStats.getPrestigeLevel(player);
        return prestigeLevel < MATERIALS_PER_STAGE ?
                prestigeLevel + 1 :
                (prestigeLevel % MATERIALS_PER_STAGE) + 1;
    }

    private Map<String, Integer> calculateUpgradeCosts(Player player, int stage, int materialIndex) {
        Map<String, Integer> costs = new LinkedHashMap<>();
        for (int i = 0; i < materialIndex; i++) {
            Material material = materialList[i];
            String materialKey = material.name().replace("_ORE", "") + "_LEVEL_" + stage; // Sicherstellen dass _LEVEL_ immer dabei ist
            int cost = (BASE_UPGRADE_COST - (i * 5)) + (pickaxeStats.getLevel(player) * LEVEL_COST_MULTIPLIER);
            costs.put(materialKey, cost);
        }
        return costs;
    }

    private boolean checkMaterialsAvailable(Player player, Map<String, Integer> costs) {
        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
            String materialKey = entry.getKey();
            int requiredAmount = entry.getValue();

            // Direkt den vollen Pfad mit _LEVEL_ verwenden
            String balancePath = "balance." + player.getUniqueId() + "." + materialKey;

            int balance = Main.getInstance().getConfig().getInt(balancePath);
            if (balance < requiredAmount) {
                return false;
            }
        }
        return true;
    }

    private void deductMaterials(Player player, Map<String, Integer> costs) {
        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
            String materialKey = entry.getKey();
            int cost = entry.getValue();

            // Direkt den vollen Pfad mit _LEVEL_ verwenden
            String balancePath = "balance." + player.getUniqueId() + "." + materialKey;

            int currentBalance = Main.getInstance().getConfig().getInt(balancePath);
            Main.getInstance().getConfig().set(balancePath, currentBalance - cost);
        }
        Main.getInstance().saveConfig();
    }

    private void addUpgradeInfo(Player player, List<String> lore) {
        // Add abbaurate upgrade info
        if (isEven(pickaxeStats.getLevel(player) + 1)) {
            lore.add("§a- Abbaurate: §b" + pickaxeStats.getAbbaurate(player) + " §a➜ §b" +
                    (pickaxeStats.getAbbaurate(player) + 1));
        }
//        } else {
//            lore.add("§a- Abbaurate: §b" + pickaxeStats.getAbbaurate(player));
//        }

        // Add efficiency upgrade info
        String nextLevel = String.valueOf(pickaxeStats.getLevel(player) + 1);
        if (nextLevel.endsWith("5") && pickaxeStats.getEfficiency(player) != 150) {
            lore.add("§a- Effizienz: §b" + pickaxeStats.getEfficiency(player) + " §a➜ §b" +
                    (pickaxeStats.getEfficiency(player) + 1));
        }
//        } else {
//            lore.add("§a- Effizienz: §b" + pickaxeStats.getEfficiency(player));
//        }

        // Add speed upgrade info
        if (nextLevel.endsWith("0") && pickaxeStats.getSpeed(player) != 5) {
            lore.add("§a- Geschwindigkeit: §b" + pickaxeStats.getSpeed(player) + " §a➜ §b" +
                    (pickaxeStats.getSpeed(player) + 1));
        }
//        } else {
//            lore.add("§a- Geschwindigkeit: §b" + pickaxeStats.getSpeed(player));
//        }

        if (Integer.parseInt(nextLevel) == 100) {
            if (pickaxeStats.getPrestigeLevel(player) +1 % 4 == 0 && pickaxeStats.getBlockabbau(player) != 50) {
                lore.add("§a- Blockabbau: §b" + pickaxeStats.getBlockabbau(player) + " §a➜ §b" +
                        (pickaxeStats.getBlockabbau(player) + 1));
            }
        }

    }

    private void addMaterialCosts(Player player, List<String> lore) {
        int stage = (int) Math.floor(pickaxeStats.getPrestigeLevel(player) / MATERIALS_PER_STAGE);
        int materialIndex = calculateMaterialIndex(player);

        lore.add("§6Benötigte Materialien:");
        for (int i = 0; i < materialIndex; i++) {
            Material material = materialList[i];
            String materialName = material.name().replace("_ORE", "");
            int cost = (BASE_UPGRADE_COST - (i * 5)) + (pickaxeStats.getLevel(player) * LEVEL_COST_MULTIPLIER);

            // Neuer Code: Level-spezifische Materialanzeige
            String levelSuffix = "_LEVEL_" + stage;
            String displayName = materialName + levelSuffix;

            int balance = Main.getInstance().getConfig().getInt("balance." + player.getUniqueId() + "." + displayName);

            String costLine;
            if (balance >= cost) {
                costLine = String.format("§e- %s: §a%d§7/§a%d", displayName, balance, cost);
            } else {
                costLine = String.format("§e- %s: §c%d§7/§a%d", displayName, balance, cost);
            }
            lore.add(costLine);
        }
    }
}