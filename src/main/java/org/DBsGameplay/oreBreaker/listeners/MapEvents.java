package org.DBsGameplay.oreBreaker.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class MapEvents implements Listener {

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                Location playerLoc = player.getLocation();
                World world = player.getWorld();

                // Hole die X- und Z-Koordinaten des Spielers
                double x = playerLoc.getX();
                double z = playerLoc.getZ();

                // Finde den höchsten Block an dieser Position
                int y = world.getHighestBlockYAt((int) x, (int) z);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent e) {
        e.setFoodLevel(20);
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType() == Material.NETHERITE_PICKAXE) {
            e.setCancelled(true);
        }
    }
}
