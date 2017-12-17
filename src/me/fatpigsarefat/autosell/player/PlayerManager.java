package me.fatpigsarefat.autosell.player;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerManager {

    private ArrayList<ASPlayer> players = new ArrayList<>();

    public void addPlayer(ASPlayer player) {
        players.add(player);
    }

    public boolean isPlayerLoaded(Player player) {
        for (ASPlayer asPlayer : players) {
            if (asPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void removePlayer(ASPlayer player) {
        players.remove(player);
    }

    public ASPlayer getPlayer(Player player) {
        for (ASPlayer asPlayer : players) {
            if (asPlayer.getPlayer().equals(player)) {
                return asPlayer;
            }
        }

        ASPlayer asPlayer = new ASPlayer(player);
        addPlayer(asPlayer);
        return getPlayer(player);
    }

    public ArrayList<ASPlayer> getPlayers() {
        return players;
    }
}