package org.DBsGameplay.oreBreaker.command;

import org.DBsGameplay.oreBreaker.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ExchangeCommand implements CommandExecutor, TabCompleter {
    private final Map<String, String[]> materialLevels = new HashMap<>();

    public ExchangeCommand() {
        // ... (Deine materialLevels-Initialisierung bleibt gleich) ...
        materialLevels.put("COAL", new String[]{
                "COAL_LEVEL_1", "COAL_LEVEL_2", "COAL_LEVEL_3", "COAL_LEVEL_4", "COAL_LEVEL_5", "COAL_LEVEL_6",
                "COAL_LEVEL_7", "COAL_LEVEL_8", "COAL_LEVEL_9", "COAL_LEVEL_10", "COAL_LEVEL_11", "COAL_LEVEL_12",
                "COAL_LEVEL_13", "COAL_LEVEL_14", "COAL_LEVEL_15", "COAL_LEVEL_16", "COAL_LEVEL_17", "COAL_LEVEL_18",
                "COAL_LEVEL_19", "COAL_LEVEL_20", "COAL_LEVEL_21", "COAL_LEVEL_22", "COAL_LEVEL_23", "COAL_LEVEL_24",
                "COAL_LEVEL_25", "COAL_LEVEL_26", "COAL_LEVEL_27", "COAL_LEVEL_28", "COAL_LEVEL_29", "COAL_LEVEL_30"
        });
        materialLevels.put("IRON", new String[]{
                "IRON_LEVEL_1", "IRON_LEVEL_2", "IRON_LEVEL_3", "IRON_LEVEL_4", "IRON_LEVEL_5", "IRON_LEVEL_6",
                "IRON_LEVEL_7", "IRON_LEVEL_8", "IRON_LEVEL_9", "IRON_LEVEL_10", "IRON_LEVEL_11", "IRON_LEVEL_12",
                "IRON_LEVEL_13", "IRON_LEVEL_14", "IRON_LEVEL_15", "IRON_LEVEL_16", "IRON_LEVEL_17", "IRON_LEVEL_18",
                "IRON_LEVEL_19", "IRON_LEVEL_20", "IRON_LEVEL_21", "IRON_LEVEL_22", "IRON_LEVEL_23", "IRON_LEVEL_24",
                "IRON_LEVEL_25", "IRON_LEVEL_26", "IRON_LEVEL_27", "IRON_LEVEL_28", "IRON_LEVEL_29", "IRON_LEVEL_30"
        });
        materialLevels.put("COPPER", new String[]{
                "COPPER_LEVEL_1", "COPPER_LEVEL_2", "COPPER_LEVEL_3", "COPPER_LEVEL_4", "COPPER_LEVEL_5", "COPPER_LEVEL_6",
                "COPPER_LEVEL_7", "COPPER_LEVEL_8", "COPPER_LEVEL_9", "COPPER_LEVEL_10", "COPPER_LEVEL_11", "COPPER_LEVEL_12",
                "COPPER_LEVEL_13", "COPPER_LEVEL_14", "COPPER_LEVEL_15", "COPPER_LEVEL_16", "COPPER_LEVEL_17", "COPPER_LEVEL_18",
                "COPPER_LEVEL_19", "COPPER_LEVEL_20", "COPPER_LEVEL_21", "COPPER_LEVEL_22", "COPPER_LEVEL_23", "COPPER_LEVEL_24",
                "COPPER_LEVEL_25", "COPPER_LEVEL_26", "COPPER_LEVEL_27", "COPPER_LEVEL_28", "COPPER_LEVEL_29", "COPPER_LEVEL_30"
        });
        materialLevels.put("GOLD", new String[]{
                "GOLD_LEVEL_1", "GOLD_LEVEL_2", "GOLD_LEVEL_3", "GOLD_LEVEL_4", "GOLD_LEVEL_5", "GOLD_LEVEL_6",
                "GOLD_LEVEL_7", "GOLD_LEVEL_8", "GOLD_LEVEL_9", "GOLD_LEVEL_10", "GOLD_LEVEL_11", "GOLD_LEVEL_12",
                "GOLD_LEVEL_13", "GOLD_LEVEL_14", "GOLD_LEVEL_15", "GOLD_LEVEL_16", "GOLD_LEVEL_17", "GOLD_LEVEL_18",
                "GOLD_LEVEL_19", "GOLD_LEVEL_20", "GOLD_LEVEL_21", "GOLD_LEVEL_22", "GOLD_LEVEL_23", "GOLD_LEVEL_24",
                "GOLD_LEVEL_25", "GOLD_LEVEL_26", "GOLD_LEVEL_27", "GOLD_LEVEL_28", "GOLD_LEVEL_29", "GOLD_LEVEL_30"
        });
        materialLevels.put("REDSTONE", new String[]{
                "REDSTONE_LEVEL_1", "REDSTONE_LEVEL_2", "REDSTONE_LEVEL_3", "REDSTONE_LEVEL_4", "REDSTONE_LEVEL_5", "REDSTONE_LEVEL_6",
                "REDSTONE_LEVEL_7", "REDSTONE_LEVEL_8", "REDSTONE_LEVEL_9", "REDSTONE_LEVEL_10", "REDSTONE_LEVEL_11", "REDSTONE_LEVEL_12",
                "REDSTONE_LEVEL_13", "REDSTONE_LEVEL_14", "REDSTONE_LEVEL_15", "REDSTONE_LEVEL_16", "REDSTONE_LEVEL_17", "REDSTONE_LEVEL_18",
                "REDSTONE_LEVEL_19", "REDSTONE_LEVEL_20", "REDSTONE_LEVEL_21", "REDSTONE_LEVEL_22", "REDSTONE_LEVEL_23", "REDSTONE_LEVEL_24",
                "REDSTONE_LEVEL_25", "REDSTONE_LEVEL_26", "REDSTONE_LEVEL_27", "REDSTONE_LEVEL_28", "REDSTONE_LEVEL_29", "REDSTONE_LEVEL_30"
        });
        materialLevels.put("EMERALD", new String[]{
                "EMERALD_LEVEL_1", "EMERALD_LEVEL_2", "EMERALD_LEVEL_3", "EMERALD_LEVEL_4", "EMERALD_LEVEL_5", "EMERALD_LEVEL_6",
                "EMERALD_LEVEL_7", "EMERALD_LEVEL_8", "EMERALD_LEVEL_9", "EMERALD_LEVEL_10", "EMERALD_LEVEL_11", "EMERALD_LEVEL_12",
                "EMERALD_LEVEL_13", "EMERALD_LEVEL_14", "EMERALD_LEVEL_15", "EMERALD_LEVEL_16", "EMERALD_LEVEL_17", "EMERALD_LEVEL_18",
                "EMERALD_LEVEL_19", "EMERALD_LEVEL_20", "EMERALD_LEVEL_21", "EMERALD_LEVEL_22", "EMERALD_LEVEL_23", "EMERALD_LEVEL_24",
                "EMERALD_LEVEL_25", "EMERALD_LEVEL_26", "EMERALD_LEVEL_27", "EMERALD_LEVEL_28", "EMERALD_LEVEL_29", "EMERALD_LEVEL_30"
        });
        materialLevels.put("LAPIS", new String[]{
                "LAPIS_LEVEL_1", "LAPIS_LEVEL_2", "LAPIS_LEVEL_3", "LAPIS_LEVEL_4", "LAPIS_LEVEL_5", "LAPIS_LEVEL_6",
                "LAPIS_LEVEL_7", "LAPIS_LEVEL_8", "LAPIS_LEVEL_9", "LAPIS_LEVEL_10", "LAPIS_LEVEL_11", "LAPIS_LEVEL_12",
                "LAPIS_LEVEL_13", "LAPIS_LEVEL_14", "LAPIS_LEVEL_15", "LAPIS_LEVEL_16", "LAPIS_LEVEL_17", "LAPIS_LEVEL_18",
                "LAPIS_LEVEL_19", "LAPIS_LEVEL_20", "LAPIS_LEVEL_21", "LAPIS_LEVEL_22", "LAPIS_LEVEL_23", "LAPIS_LEVEL_24",
                "LAPIS_LEVEL_25", "LAPIS_LEVEL_26", "LAPIS_LEVEL_27", "LAPIS_LEVEL_28", "LAPIS_LEVEL_29", "LAPIS_LEVEL_30"
        });
        materialLevels.put("DIAMOND", new String[]{
                "DIAMOND_LEVEL_1", "DIAMOND_LEVEL_2", "DIAMOND_LEVEL_3", "DIAMOND_LEVEL_4", "DIAMOND_LEVEL_5", "DIAMOND_LEVEL_6",
                "DIAMOND_LEVEL_7", "DIAMOND_LEVEL_8", "DIAMOND_LEVEL_9", "DIAMOND_LEVEL_10", "DIAMOND_LEVEL_11", "DIAMOND_LEVEL_12",
                "DIAMOND_LEVEL_13", "DIAMOND_LEVEL_14", "DIAMOND_LEVEL_15", "DIAMOND_LEVEL_16", "DIAMOND_LEVEL_17", "DIAMOND_LEVEL_18",
                "DIAMOND_LEVEL_19", "DIAMOND_LEVEL_20", "DIAMOND_LEVEL_21", "DIAMOND_LEVEL_22", "DIAMOND_LEVEL_23", "DIAMOND_LEVEL_24",
                "DIAMOND_LEVEL_25", "DIAMOND_LEVEL_26", "DIAMOND_LEVEL_27", "DIAMOND_LEVEL_28", "DIAMOND_LEVEL_29", "DIAMOND_LEVEL_30"
        });

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length != 3) {
            player.sendMessage("Verwendung: /umtausch <Material> <vonStufe> <zuStufe>");
            return true;
        }

        String material = args[0].toUpperCase();
        int selectedLevel;
        int targetedLevel;

        if (!materialLevels.containsKey(material)) {
            player.sendMessage("Das angegebene Material ist nicht gültig.");
            return true;
        }

        try {
            selectedLevel = Integer.parseInt(args[1]);
            targetedLevel = Integer.parseInt(args[2]);

            if (selectedLevel < 0 || selectedLevel > 30 || targetedLevel < 0 || targetedLevel > 30) {
                player.sendMessage("Die Stufe muss zwischen 0 und 30 liegen.");
                return true;
            }

            if (targetedLevel == selectedLevel) {
                player.sendMessage("Die beiden Level dürfen nicht gleich sein.");
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Bitte gib eine gültige Stufe an (0-30).");
            return true;
        }

        performMaterialExchange(player, material, selectedLevel, targetedLevel);
        return false;
    }

    private void performMaterialExchange(Player player, String material, int selectedLevel, int targetedLevel) {
        String selectedName = material + "_LEVEL_" + selectedLevel;
        String targetedName = material + "_LEVEL_" + targetedLevel;

        // Ändere die Logik hier - verwende immer _LEVEL_ Format
        if (selectedLevel == 0) {
            selectedName = material + "_LEVEL_0";
        }
        if (targetedLevel == 0) {
            targetedName = material + "_LEVEL_0";
        }

        int requiredAmount = calculateRequiredAmount(material, selectedLevel, targetedLevel);
        UUID uuid = player.getUniqueId();

        int topay = requiredAmount;
        int toget = 1;

        if (selectedLevel > targetedLevel) {
            topay = 1;
            toget = requiredAmount;
        }

        int playerBalance = Main.getInstance().getConfig().getInt("balance." + uuid + "." + selectedName, 0);
        int maxExchangeAmount = playerBalance / topay;

        if (maxExchangeAmount > 0) {
            int totalToPay = maxExchangeAmount * topay;
            int totalToGet = maxExchangeAmount * toget;

            Main.getInstance().getConfig().set("balance." + uuid + "." + selectedName, playerBalance - totalToPay);
            int newTargetedBalance = Main.getInstance().getConfig().getInt("balance." + uuid + "." + targetedName, 0) + totalToGet;
            Main.getInstance().getConfig().set("balance." + uuid + "." + targetedName, newTargetedBalance);
            Main.getInstance().saveConfig();

            player.sendMessage("§aDu hast §6" + (maxExchangeAmount) + "x §6" + selectedName + "§a auf §6" + targetedName + "§a umgetauscht. Du erhältst: §d" + totalToGet);
        } else {
            player.sendMessage("§cDu hast nicht genug §6" + selectedName + "§c um auf §6" + targetedName + "§c umzutauschen.");
        }
    }

    private int calculateRequiredAmount(String material, int selectedLevel, int targetedLevel) {
        int difference = Math.abs(targetedLevel - selectedLevel);
        return (int) Math.pow(9, difference);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(materialLevels.keySet());
        } else if (args.length == 2) {
            List list = new ArrayList<>();
            list.add("vonLevel");
            return list;
        } else if (args.length == 3) {
            List list = new ArrayList<>();
            list.add("zuLevel");
            return list;
        }
        return Collections.emptyList();
    }
}
