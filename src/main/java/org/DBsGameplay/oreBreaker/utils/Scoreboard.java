package org.DBsGameplay.oreBreaker.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

public class Scoreboard {

    public static void loadScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("test", "dummy");
        PickaxeStats pickaxeStats = new PickaxeStats();

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§8-----§6Prison§8-----");

        Score score = objective.getScore("Spitzhacke");
        score.setScore(10);
        Score score1 = objective.getScore("§7Lv. " + pickaxeStats.getLevel(player));
        score1.setScore(9);
        Score score2 = objective.getScore("§1");
        score2.setScore(8);
        Score score3 = objective.getScore("Prestige");
        score3.setScore(7);
        Score score4 = objective.getScore("§7" + pickaxeStats.getPrestigeLevel(player));
        score4.setScore(6);
        Score score5 = objective.getScore("§2");
        score5.setScore(5);
        Score score6 = objective.getScore("Abgebaute Blöcke");
        score6.setScore(4);
        Score score7 = objective.getScore("§7" + pickaxeStats.getBlocksMined(player) + " Blöcke");
        score7.setScore(3);
        player.setScoreboard(scoreboard);
    }
}
