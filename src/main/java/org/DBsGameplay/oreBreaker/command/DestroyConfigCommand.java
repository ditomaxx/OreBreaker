package org.DBsGameplay.oreBreaker.command;

import org.DBsGameplay.oreBreaker.Main;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public class DestroyConfigCommand {

    @Command("destroyconfig")
    public void destroyConfig(BukkitCommandActor actor) {
        // Config leeren
        Main.getInstance().getConfig().getKeys(false).forEach(key ->
                Main.getInstance().getConfig().set(key, null)
        );

        // Leere Config speichern
        Main.getInstance().saveConfig();

        actor.reply("§aConfig wurde erfolgreich geleert!");
    }
}
