package org.DBsGameplay.oreBreaker.command;

import org.DBsGameplay.oreBreaker.utils.MinionType;
import org.DBsGameplay.oreBreaker.utils.MinionsManager;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public class SetMinionCommand {
    MinionsManager minionsManager = new MinionsManager();

    @Command("setminion")
    @Description("Set a minion at your location")
    public void setMinion(BukkitCommandActor commandActor, MinionType minionType) {
        if (!commandActor.isPlayer()) return;

        Player player = commandActor.asPlayer();
        Location location = player.getLocation();

        minionsManager.addMinion(minionType, location);
        ArmorStand armorStand = location.getWorld().spawn(location.add(0, 1,0), ArmorStand.class, stand -> {
            stand.setVisible(false);              // Macht den ArmorStand unsichtbar
            stand.setGravity(false);             // Deaktiviert Schwerkraft
            stand.setBasePlate(false);           // Entfernt die Bodenplatte
            stand.setArms(false);                // Keine Arme anzeigen
            stand.setCustomNameVisible(true);     // Zeigt den CustomName an
            stand.setCustomName("§6" + minionType.name() + " Minion"); // Setzt den Namen
            stand.setInvulnerable(true);         // Macht ihn unverwundbar
            stand.setCanPickupItems(false);      // Kann keine Items aufheben
            stand.setMarker(true);               // Verhindert Kollisionen
        });
    }
}
