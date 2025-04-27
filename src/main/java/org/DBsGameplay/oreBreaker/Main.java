package org.DBsGameplay.oreBreaker;

import org.DBsGameplay.oreBreaker.command.ExchangeCommand;
import org.DBsGameplay.oreBreaker.command.SetSpawnCommand;
import org.DBsGameplay.oreBreaker.listeners.Events;
import org.DBsGameplay.oreBreaker.listeners.MapEvents;
import org.DBsGameplay.oreBreaker.utils.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveConfig();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getPluginManager().registerEvents(new MapEvents(), this);

        getCommand("umtausch").setExecutor(new ExchangeCommand());
        getCommand("setspawn").setExecutor(new SetSpawnCommand());

        getCommand("umtausch").setTabCompleter(new ExchangeCommand());

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Scoreboard.loadScoreboard(player);
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    public static Main getInstance(){
        return instance;
    }

}
