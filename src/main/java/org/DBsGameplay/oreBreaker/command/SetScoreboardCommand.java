package org.DBsGameplay.oreBreaker.command;

import org.DBsGameplay.oreBreaker.Main;
import org.DBsGameplay.oreBreaker.utils.PickaxeStats;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SetScoreboardCommand implements CommandExecutor {
    private final PickaxeStats pickaxeStats = new PickaxeStats();
    private static final int HOLOGRAM_LINES = 11;
    private Location lastLocation;
    private LeaderboardType currentType;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage("§cBenutzung: /leaderboard <blocks/prestige>");
            return true;
        }

        lastLocation = player.getLocation();

        switch (args[0].toLowerCase()) {
            case "blocks":
                currentType = LeaderboardType.BLOCKS;
                createHologram(lastLocation, currentType);
                saveLeaderboard(lastLocation, currentType);
                startUpdateTask(lastLocation, currentType);
                break;
            case "prestige":
                currentType = LeaderboardType.PRESTIGE;
                createHologram(lastLocation, currentType);
                saveLeaderboard(lastLocation, currentType);
                startUpdateTask(lastLocation, currentType);
                break;
            default:
                player.sendMessage("§cBitte wähle 'blocks' oder 'prestige'!");
        }

        return true;
    }

    private void startUpdateTask(Location loc, LeaderboardType type) {
        new BukkitRunnable() {
            @Override
            public void run() {
                removeExistingHolograms(loc, type);
                createHologram(loc, type);
            }
        }.runTaskTimer(Main.getInstance(), 200L, 200L); // 200 Ticks = 10 Sekunden
    }

    private void createHologram(Location loc, LeaderboardType type) {
        List<LeaderboardEntry> entries = new ArrayList<>();

        // Lade alle Spieler aus der Config
        if (Main.getInstance().getConfig().contains("players")) {
            for (String uuid : Main.getInstance().getConfig().getConfigurationSection("players").getKeys(false)) {
                String path = "players." + uuid;
                String playerName = Main.getInstance().getConfig().contains(path + ".name") ?
                        Main.getInstance().getConfig().getString(path + ".name") : uuid;

                if (playerName != null) {
                    int value;
                    if (type == LeaderboardType.BLOCKS) {
                        value = Main.getInstance().getConfig().getInt(path + ".blocksMined", 0);
                    } else {
                        value = Main.getInstance().getConfig().getInt(path + ".prestigeLevel", 0);
                    }
                    entries.add(new LeaderboardEntry(playerName, value));
                }
            }
        }

        entries.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));

        // Header Hologram
        String title = type == LeaderboardType.BLOCKS ? "Blöcke" : "Prestige";
        ArmorStand header = createArmorStand(loc.clone().add(0, 2 + (HOLOGRAM_LINES * 0.25), 0));
        header.setCustomName("§8----------§6" + title + " Leaderboard§8----------");

        // Entry Holograms
        for (int i = 0; i < Math.min(10, entries.size()); i++) {
            LeaderboardEntry entry = entries.get(i);
            Location entryLoc = loc.clone().add(0, 1 + ((HOLOGRAM_LINES - i - 1) * 0.25), 0);
            ArmorStand entryStand = createArmorStand(entryLoc);
            entryStand.setCustomName(String.format("§e#%d §7%s §8[§b%d§8]",
                    i + 1, entry.getPlayerName(), entry.getValue()));
        }

        // Footer Hologram
        ArmorStand footer = createArmorStand(loc.clone());
        footer.setCustomName("§8-----------------------------------");
    }

    private ArmorStand createArmorStand(Location loc) {
        ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
        stand.setGravity(false);
        stand.setCanPickupItems(false);
        stand.setCustomNameVisible(true);
        stand.setVisible(false);
        stand.setSmall(true);
        return stand;
    }

    private void removeExistingHolograms(Location loc, LeaderboardType type) {
        loc.getWorld().getEntities().stream()
                .filter(entity -> entity instanceof ArmorStand)
                .filter(entity -> entity.getLocation().distance(loc) < 5)
                .filter(entity -> entity.getCustomName() != null && entity.getCustomName().contains(type.name()))
                .forEach(Entity::remove);
    }

    private enum LeaderboardType {
        BLOCKS,
        PRESTIGE
    }

    private static class LeaderboardEntry {
        private final String playerName;
        private final int value;

        public LeaderboardEntry(String playerName, int value) {
            this.playerName = playerName;
            this.value = value;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getValue() {
            return value;
        }
    }

    private void saveLeaderboard(Location loc, LeaderboardType type) {
        String path = "leaderboards." + type.name().toLowerCase() + "." + loc.getWorld().getName() + "." + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
        Main.getInstance().getConfig().set(path, true);
        Main.getInstance().saveConfig();
    }

    public void loadLeaderboards() {
        for (LeaderboardType type : LeaderboardType.values()) {
            String typePath = "leaderboards." + type.name().toLowerCase();
            if (Main.getInstance().getConfig().contains(typePath)) {
                for (String worldName : Main.getInstance().getConfig().getConfigurationSection(typePath).getKeys(false)) {
                    for (String locKey : Main.getInstance().getConfig().getConfigurationSection(typePath + "." + worldName).getKeys(false)) {
                        String[] coords = locKey.split("_");
                        int x = Integer.parseInt(coords[0]);
                        int y = Integer.parseInt(coords[1]);
                        int z = Integer.parseInt(coords[2]);

                        Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
                        createHologram(loc, type);
                    }
                }
            }
        }
    }
}
