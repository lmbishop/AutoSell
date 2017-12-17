package me.fatpigsarefat.autosell.player;

import me.fatpigsarefat.autosell.AutoSell;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ASPlayer asPlayer = new ASPlayer(event.getPlayer());
        if (!AutoSell.getPlayerManager().isPlayerLoaded(event.getPlayer())) {
            AutoSell.getPlayerManager().addPlayer(asPlayer);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (AutoSell.getPlayerManager().isPlayerLoaded(event.getPlayer())) {
            AutoSell.getPlayerManager().removePlayer(AutoSell.getPlayerManager().getPlayer(event.getPlayer()));
        }
    }

}
