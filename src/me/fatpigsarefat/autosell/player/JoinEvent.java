package me.fatpigsarefat.autosell.player;

import me.fatpigsarefat.autosell.AutoSell;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ASPlayer asPlayer = new ASPlayer(event.getPlayer());
        if (!AutoSell.getPlayerManager().isPlayerLoaded(event.getPlayer())) {
            AutoSell.getPlayerManager().addPlayer(asPlayer);
        }
        File file = new File(AutoSell.getInstance().getDataFolder() + File.separator + "data.yml");
        if (file.exists()) {
            if (event.getPlayer().hasPermission("autosell.admin")) {
                event.getPlayer().sendMessage(ChatColor.RED + "Old AutoSell data detected - /autosell migratedata to migrate that data.");
                event.getPlayer().sendMessage(ChatColor.RED + "WARNING - do not resend the command if you previously have done it, instead delete the old " +
                        "data file.");
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (AutoSell.getPlayerManager().isPlayerLoaded(event.getPlayer())) {
            AutoSell.getPlayerManager().removePlayer(AutoSell.getPlayerManager().getPlayer(event.getPlayer()));
        }
    }

}
