package me.fatpigsarefat.autosell;

import me.fatpigsarefat.autosell.chests.SellChest;
import me.fatpigsarefat.autosell.chests.SellChestManager;
import me.fatpigsarefat.autosell.chests.SellSignFormat;
import me.fatpigsarefat.autosell.commands.AutosellCommand;
import me.fatpigsarefat.autosell.events.BreakEvent;
import me.fatpigsarefat.autosell.events.InteractEvent;
import me.fatpigsarefat.autosell.events.SignCreateEvent;
import me.fatpigsarefat.autosell.notifications.NotificationDispatcher;
import me.fatpigsarefat.autosell.player.JoinEvent;
import me.fatpigsarefat.autosell.player.PlayerManager;
import me.fatpigsarefat.autosell.utils.Config;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AutoSell extends JavaPlugin {

    private static AutoSell instance;
    private static SellChestManager sellChestManager;
    private static PlayerManager playerManager;
    private static NotificationDispatcher notificationDispatcher;
    private static Economy economy = null;
    private static HashMap<String, Double> prices;

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    @Override
    public void onEnable() {
        instance = this;
        prices = new HashMap<>();
        sellChestManager = new SellChestManager();
        playerManager = new PlayerManager();
        notificationDispatcher = new NotificationDispatcher();
        new SellSignFormat();
        dataGenerator();
        setupEconomy();
        File essentials = new File("plugins" +  File.separator + "Essentials"  + File.separator +  "worth.yml");
        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        Config c = new Config();
        if (Config.essentialsWorth) {
            c.parseConfigFile(essentials, "worth");
        } else {
            c.parseConfigFile(config, "prices");
        }

        File dataDir = new File(this.getDataFolder() + File.separator + "data");
        if (dataDir.isDirectory()) {
            File[] dataFiles = dataDir.listFiles();
            if (dataFiles != null) {
                for (File file : dataFiles) {
                    int dot = file.getName().lastIndexOf(".");
                    if (file.getName().substring(dot + 1).equalsIgnoreCase("yml")) {
                        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                        UUID owner = UUID.fromString(file.getName().replace(".yml", ""));
                        if (yaml.contains("chests")) {
                            for (String s : yaml.getConfigurationSection("chests").getKeys(false)) {
                                String[] parts = s.split(", ");
                                Location locChest = new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]),
                                        Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                                String sign = yaml.getString("chests." + s + ".sign");
                                String[] signParts = sign.split(", ");
                                Location locSign = new Location(Bukkit.getWorld(signParts[0]), Integer.parseInt(signParts[1]),
                                        Integer.parseInt(signParts[2]), Integer.parseInt(signParts[3]));
                                int cooldown = yaml.getInt("chests." + s + ".cooldown");
                                List<String> members = yaml.getStringList("chests." + s + ".members");
                                List<UUID> membersUUID = new ArrayList<>();
                                for (String member : members) {
                                    membersUUID.add(UUID.fromString(member));
                                }

                                SellChest sellChest = new SellChest(cooldown, locChest, locSign, owner, membersUUID);
                                sellChestManager.registerSellChest(sellChest);
                            }
                        }
                    }
                }
            }
        } else {
            dataDir.mkdir();
        }

        Bukkit.getPluginCommand("autosell").setExecutor(new AutosellCommand());
        Bukkit.getPluginManager().registerEvents(new SignCreateEvent(), this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BreakEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);
        sellChestManager.runCooldown();
        sellChestManager.runAutosave();
        notificationDispatcher.startAutomaticNotifier();

        File file = new File(this.getDataFolder() + File.separator + "data.yml");
        if (file.exists()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("autosell.admin")) {
                    player.sendMessage(ChatColor.RED + "Old AutoSell data detected - /autosell migratedata to migrate that data.");
                    player.sendMessage(ChatColor.RED + "WARNING - do not resend the command if you previously have done it, instead delete the old data file.");
                }
            }
        }
    }

    @Override
    public void onDisable() {
        sellChestManager.save();
    }

    public static AutoSell getInstance() {
        return instance;
    }

    public static HashMap<String, Double> getPrices() {
        return prices;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static SellChestManager getSellChestManager() {
        return sellChestManager;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static NotificationDispatcher getNotificationDispatcher() {
        return notificationDispatcher;
    }

    private void dataGenerator() {
        saveDefaultConfig();
    }
}
