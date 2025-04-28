package org.DBsGameplay.oreBreaker.utils;

import org.DBsGameplay.oreBreaker.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;

public class Scoreboard {

    public static void loadScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("test", "dummy");
        PickaxeStats pickaxeStats = new PickaxeStats();

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§8-----§6Prison§8-----");

        Score score = objective.getScore("Spitzhacke");
        score.setScore(13);
        Score score1 = objective.getScore("§7Lv. " + pickaxeStats.getLevel(player));
        score1.setScore(12);
        Score score2 = objective.getScore("§1");
        score2.setScore(11);
        Score score3 = objective.getScore("Prestige");
        score3.setScore(10);
        Score score4 = objective.getScore("§7" + pickaxeStats.getPrestigeLevel(player));
        score4.setScore(9);
        Score score5 = objective.getScore("§2");
        score5.setScore(8);
        Score score6 = objective.getScore("Abgebaute Blöcke");
        score6.setScore(7);
        Score score7 = objective.getScore("§7" + pickaxeStats.getBlocksMined(player) + " Blöcke");
        score7.setScore(6);

        // Trennlinie
        Score score8 = objective.getScore("§3");
        score8.setScore(5);

        // Blöcke Ranking
        RankInfo blockRankInfo = calculateBlockRank(player);
        String blockRankLine = String.format("§e#%d §7Blöcke §8[§b%d§8]",
                blockRankInfo.rank, pickaxeStats.getBlocksMined(player));
        Score blockRankScore = objective.getScore(blockRankLine);
        blockRankScore.setScore(4);

        // Prestige Ranking
        RankInfo prestigeRankInfo = calculatePrestigeRank(player);
        String prestigeRankLine = String.format("§e#%d §7Prestige §8[§b%d§8]",
                prestigeRankInfo.rank, pickaxeStats.getPrestigeLevel(player));
        Score prestigeRankScore = objective.getScore(prestigeRankLine);
        prestigeRankScore.setScore(3);

        player.setScoreboard(scoreboard);
    }

    private static RankInfo calculateBlockRank(Player player) {
        FileConfiguration config = Main.getInstance().getConfig();
        List<PlayerScore> scores = new ArrayList<>();

        // Alle Spieler aus der Config laden
        if (config.contains("players")) {
            for (String uuid : config.getConfigurationSection("players").getKeys(false)) {
                int blocks = config.getInt("players." + uuid + ".blocksMined", 0);
                scores.add(new PlayerScore(uuid, blocks));
            }
        }

        // Nach Blöcken sortieren
        scores.sort((p1, p2) -> Integer.compare(p2.score, p1.score));

        // Rang des Spielers finden
        int rank = 1;
        for (PlayerScore score : scores) {
            if (score.uuid.equals(player.getUniqueId().toString())) {
                return new RankInfo(rank, score.score);
            }
            rank++;
        }

        return new RankInfo(rank, 0);
    }

    private static RankInfo calculatePrestigeRank(Player player) {
        FileConfiguration config = Main.getInstance().getConfig();
        List<PlayerScore> scores = new ArrayList<>();

        // Alle Spieler aus der Config laden
        if (config.contains("players")) {
            for (String uuid : config.getConfigurationSection("players").getKeys(false)) {
                int prestige = config.getInt("players." + uuid + ".prestigeLevel", 0);
                scores.add(new PlayerScore(uuid, prestige));
            }
        }

        // Nach Prestige sortieren
        scores.sort((p1, p2) -> Integer.compare(p2.score, p1.score));

        // Rang des Spielers finden
        int rank = 1;
        for (PlayerScore score : scores) {
            if (score.uuid.equals(player.getUniqueId().toString())) {
                return new RankInfo(rank, score.score);
            }
            rank++;
        }

        return new RankInfo(rank, 0);
    }

    private static class PlayerScore {
        final String uuid;
        final int score;

        PlayerScore(String uuid, int score) {
            this.uuid = uuid;
            this.score = score;
        }
    }

    private static class RankInfo {
        final int rank;
        final int score;

        RankInfo(int rank, int score) {
            this.rank = rank;
            this.score = score;
        }
    }
}