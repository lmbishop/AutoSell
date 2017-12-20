package me.fatpigsarefat.autosell.utils;

import me.fatpigsarefat.autosell.AutoSell;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    public static boolean essentialsWorth;
    public static int sellTimer;
    public static boolean sellchestEnabled;
    public static boolean sellcommandEnabled;
    public static String sellchestMode;
    public static boolean sellchestProection;
    public static int signPrice;
    public static double priceMultiplier;
    public static boolean showSellNotificationIfZero;
    public static boolean showDetailedOverview;
    public static boolean limit;
    public static int limitMax;
    public static int boosterMax;

    public Config() {
        essentialsWorth = AutoSell.getInstance().getConfig().getBoolean("use-essentials-worth-yml");
        sellTimer = AutoSell.getInstance().getConfig().getInt("sell-timer");
        sellchestEnabled = AutoSell.getInstance().getConfig().getBoolean("sellchest-enabled");
        sellcommandEnabled = AutoSell.getInstance().getConfig().getBoolean("sellcommand-enabled");
        sellchestMode = AutoSell.getInstance().getConfig().getString("sellchest-mode");
        sellchestProection = AutoSell.getInstance().getConfig().getBoolean("sellchest-protection");
        signPrice = AutoSell.getInstance().getConfig().getInt("sign-price");
        priceMultiplier = AutoSell.getInstance().getConfig().getDouble("price-multiplier");
        showSellNotificationIfZero = AutoSell.getInstance().getConfig().getBoolean("show-sell-notification-if-zero");
        showDetailedOverview = AutoSell.getInstance().getConfig().getBoolean("show-detailed-overview");
        limit = AutoSell.getInstance().getConfig().getBoolean("limit");
        limitMax = AutoSell.getInstance().getConfig().getInt("limit-max");
        boosterMax = AutoSell.getInstance().getConfig().getInt("booster-max");
    }

    public void parseConfigFile(File file, String rootKey) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String item : config.getConfigurationSection(rootKey).getKeys(false)) {
            if (config.isConfigurationSection(rootKey + "." + item)) {
                for (String itemdata : config.getConfigurationSection(rootKey + "." + item).getKeys(false)) {
                    String itemReformat = item;
                    Double price = config.getDouble(rootKey + "." + item + "." + itemdata);

                    boolean isItemId = false;
                    try {
                        Integer.parseInt(itemReformat);
                        isItemId = true;
                    } catch (NumberFormatException ignored) {

                    }
                    if (isItemId) {
                        try {
                            itemReformat = Material.getMaterial(Integer.parseInt(itemReformat)).toString();
                        } catch (NullPointerException ignored) {

                        }
                    }
                    itemReformat = itemReformat.replace("_", "").toLowerCase();

                    System.out.println("[AutoSell] Item " + itemReformat + " registered for $" + price);
                    AutoSell.getPrices().put(itemReformat + ":" + itemdata, price);
                }
            } else {
                String itemReformat = item;
                Double price = config.getDouble(rootKey + "." + item);

                boolean isItemId = false;
                try {
                    Integer.parseInt(itemReformat);
                    isItemId = true;
                } catch (NumberFormatException ignored) {

                }
                if (isItemId) {
                    try {
                        itemReformat = Material.getMaterial(Integer.parseInt(itemReformat)).toString();
                    } catch (NullPointerException ignored) {

                    }
                }
                itemReformat = itemReformat.replace("_", "").toLowerCase();

                System.out.println("[AutoSell] Item " + itemReformat + " registered for $" + price);
                AutoSell.getPrices().put(itemReformat, price);
            }
        }
        System.out.println("[AutoSell] A total of " + AutoSell.getPrices().size() + " items have been registered.");
    }
}
