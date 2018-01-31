package me.fatpigsarefat.autosell.chests;

import me.fatpigsarefat.autosell.AutoSell;
import me.fatpigsarefat.autosell.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SellChestManager {

    private BukkitTask cooldownRunnable = null;
    private BukkitTask autosaveRunnable = null;
    private ArrayList<SellChest> sellChests = new ArrayList<>();

    public void registerSellChest(SellChest sellChest) {
        sellChests.add(sellChest);
    }

    public void unregisterSellChest(SellChest sellChest) {
        sellChests.remove(sellChest);
    }

    public ArrayList<SellChest> getSellChests() {
        return sellChests;
    }

    public void runCooldown() {
        if (cooldownRunnable == null) {
            cooldownRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (Config.sellchestMode.equalsIgnoreCase("RIGHTCLICKSELL")) {
                        for (SellChest chest : sellChests) {
                            if (chest != null && chest.getChestLocation() != null &&
                                    chest.getChestLocation().getChunk() != null &&
                                    chest.getChestLocation().getChunk().isLoaded()) {
                                if (chest.getCooldown() > 0) {
                                    chest.decrementCooldown();
                                }
                                chest.updateSign();
                            }
                        }
                    } else {
                        for (SellChest chest : sellChests) {
                            if (chest != null && chest.getChestLocation() != null &&
                                    chest.getChestLocation().getChunk() != null &&
                                    chest.getChestLocation().getChunk().isLoaded()) {
                                chest.decrementCooldown();
                                if (chest.getCooldown() < 0) {
                                    chest.executeSale();
                                    chest.setCooldown(Config.sellTimer);
                                }
                                chest.updateSign();
                            }
                        }
                    }
                }
            }.runTaskTimer(AutoSell.getInstance(), 20L, 20L);
        }
    }

    public void runAutosave() {
        if (autosaveRunnable == null) {
            autosaveRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    save();
                }
            }.runTaskTimer(AutoSell.getInstance(), 6000L, 6000L);
        }
    }

    public void save() {
        File dataDir = new File(AutoSell.getInstance().getDataFolder() + File.separator + "data");
        if (!dataDir.isDirectory()) {
            dataDir.mkdir();
        }
        HashMap<UUID, File> loadedFiles = new HashMap<>();
        HashMap<UUID, YamlConfiguration> loadedConfigs = new HashMap<>();
        for (SellChest sellChest : sellChests) {
            if (!loadedFiles.containsKey(sellChest.getOwner()) || !loadedConfigs.containsKey(sellChest.getOwner())) {
                File file = new File(AutoSell.getInstance().getDataFolder() + File.separator + "data" + File.separator + sellChest.getOwner().toString() + ".yml");

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
                configuration.set("chests", null);
                loadedFiles.put(sellChest.getOwner(), file);
                loadedConfigs.put(sellChest.getOwner(), configuration);
            }

        }
        for (SellChest sellChest : sellChests) {
            YamlConfiguration configuration = loadedConfigs.get(sellChest.getOwner());

            Location locChest = sellChest.getChestLocation();
            String locChestS = locChest.getWorld().getName() + ", " + locChest.getBlockX() + ", " + locChest.getBlockY() + ", "+ locChest.getBlockZ();
            Location locSign = sellChest.getSignLocation();
            String locSignS = locSign.getWorld().getName() + ", " + locSign.getBlockX() + ", " + locSign.getBlockY() + ", "+ locSign.getBlockZ();
            int cooldown = sellChest.getCooldown();
            List<String> members = new ArrayList<>();
            List<UUID> membersUUID = sellChest.getMembers();
            for (UUID member : membersUUID) {
                members.add(member.toString());
            }

            configuration.set("chests." + locChestS + ".sign", locSignS);
            configuration.set("chests." + locChestS + ".cooldown", cooldown);
            configuration.set("chests." + locChestS + ".members", members);
        }
        for (Map.Entry<UUID, YamlConfiguration> entry : loadedConfigs.entrySet()) {
            UUID uuid = entry.getKey();
            YamlConfiguration value = entry.getValue();

            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                value.set("notifications", AutoSell.getPlayerManager().getPlayer(Bukkit.getPlayer(uuid)).isSubscribedToNotifications());
            }

            try {
                value.save(loadedFiles.get(uuid));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BukkitTask getCooldownRunnable() {
        return cooldownRunnable;
    }

    public BukkitTask getAutosaveRunnable() {
        return autosaveRunnable;
    }
}
