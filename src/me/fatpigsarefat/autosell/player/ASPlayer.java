package me.fatpigsarefat.autosell.player;

import me.fatpigsarefat.autosell.AutoSell;
import me.fatpigsarefat.autosell.chests.SellChest;
import me.fatpigsarefat.autosell.utils.Config;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ASPlayer {

    private Player player;
    private boolean subscribedToNotifications;

    public ASPlayer(Player player) {
        this.player = player;
        loadData();
    }

    public void loadData() {
        File data = new File(AutoSell.getInstance().getDataFolder() + File.separator + "data" + File.separator + player.getUniqueId().toString() + ".yml");
        if (data.exists()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(data);
            subscribedToNotifications = yamlConfiguration.getBoolean("notifications", true);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public int getMaxLimit() {
        int limit = 0;
        int maxLimit = Config.limitMax;
        while (maxLimit > 0) {
            if (maxLimit == 1) {
                limit = 1;
                break;
            }
            if (player.hasPermission("autosell.limit." + maxLimit)) {
                limit = maxLimit;
                break;
            }
            maxLimit--;
        }

        return limit;
    }

    public List<SellChest> getOwnedSellChests() {
        List<SellChest> sellChests = new ArrayList<>();
        for (SellChest sellChest : AutoSell.getSellChestManager().getSellChests()) {
            if (sellChest.getOwner().equals(player.getUniqueId())) {
                sellChests.add(sellChest);
            }
        }

        return sellChests;
    }

    public int getBooster() {
        int booster = 0;
        for (int i = 0; i < Config.boosterMax; i++) {
            if (player.hasPermission("autosell.booster." + i)) {
                booster = i;
                break;
            }
        }
        return booster;
    }

    public List<SellChest> getSharedSellChests() {
        List<SellChest> sellChests = new ArrayList<>();
        for (SellChest sellChest : AutoSell.getSellChestManager().getSellChests()) {
            if (sellChest.getMembers().contains(player.getUniqueId())) {
                sellChests.add(sellChest);
            }
        }

        return sellChests;
    }

    public boolean isSubscribedToNotifications() {
        return subscribedToNotifications;
    }

    public void setSubscribedToNotifications(boolean subscribedToNotifications) {
        this.subscribedToNotifications = subscribedToNotifications;
    }
}
