package org.DBsGameplay.oreBreaker;

import org.DBsGameplay.oreBreaker.command.*;
import org.DBsGameplay.oreBreaker.listeners.Events;
import org.DBsGameplay.oreBreaker.listeners.MapEvents;
import org.DBsGameplay.oreBreaker.listeners.MinionListener;
import org.DBsGameplay.oreBreaker.utils.MinionStats;
import org.DBsGameplay.oreBreaker.utils.MinionType;
import org.DBsGameplay.oreBreaker.utils.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.bukkit.BukkitLamp;

import java.util.UUID;

public final class Main extends JavaPlugin {

    private static Main instance;
    private MinionStats minionStats;

    @Override
    public void onEnable() {
        instance = this;
        minionStats = new MinionStats();

        saveConfig();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getPluginManager().registerEvents(new MapEvents(), this);
        Bukkit.getPluginManager().registerEvents(new MinionListener(), this);

        getCommand("umtausch").setExecutor(new ExchangeCommand());
        getCommand("setspawn").setExecutor(new SetSpawnCommand());

        getCommand("umtausch").setTabCompleter(new ExchangeCommand());
        getCommand("setScoreboard").setExecutor(new SetScoreboardCommand());

        var lamps = BukkitLamp.builder(this).build();
        lamps.register(new SetMinionCommand());
        lamps.register(new DestroyConfigCommand());

        Bukkit.getWorlds().forEach(world ->
                world.getEntities().stream()
                        .filter(entity -> entity instanceof ArmorStand)
                        .forEach(Entity::remove)
        );

        new SetScoreboardCommand().loadLeaderboards();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Scoreboard.loadScoreboard(player);
                }
            }
        }.runTaskTimer(this, 0L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (getConfig().getConfigurationSection("minionstats." + player.getUniqueId()) == null) continue;

                    for (String key : getConfig().getConfigurationSection("minionstats." + player.getUniqueId()).getKeys(false)) {
                        MinionType type = MinionType.valueOf(key);

                        // Berechne Mining-Ertrag
                        int unlockedBlocks = minionStats.getUnlockedBlocks(player, type);
                        int miningLevel = minionStats.getMiningLevel(player, type);
                        int miningAmount = unlockedBlocks * miningLevel;

                        getConfig().set("minionsmined." + player.getUniqueId() + "." + type.name(), getConfig().getInt("minionsmined." + player.getUniqueId() + "." + type.name(), 0) + miningAmount);
                        saveConfig();
                    }
                }
            }
        }.runTaskTimer(this, 0L, 100L);
    }

    public static Main getInstance(){
        return instance;
    }

}
