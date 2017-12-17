package me.fatpigsarefat.autosell.utils;

import me.fatpigsarefat.autosell.AutoSell;
import org.bukkit.ChatColor;

public enum Messages {

    CHEST_CREATE(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.chest-create"))),
    CHEST_DESTROY(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.chest-destroy"))),
    CHEST_PURCHASE(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.chest-purchase"))),
    AUTOSELL_NOTIFICATIONS_ON(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.autosell-notifications-on"))),
    AUTOSELL_NOTIFICATIONS_OFF(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.autosell-notifications-off"))),
    AUTOSELL_SALE_HEADER(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.autosell-sale.header"))),
    AUTOSELL_DETAIL_OWN_CHESTS(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.autosell-sale.detail.own-chests"))),
    AUTOSELL_DETAIL_OWN_CHESTS_SHARED(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.autosell-sale.detail.own-chests-shared"))),
    AUTOSELL_DETAIL_SHARED_CHESTS(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.autosell-sale.detail.shared-chests"))),
    AUTOSELL_DETAIL_BOOSTER(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.autosell-sale.detail.booster"))),
    AUTOSELL_DETAIL_PRICE_MULTIPLIER(ChatColor.translateAlternateColorCodes('&', AutoSell.getInstance().getConfig().getString("messages.autosell-sale.detail.price-multiplier")));

    private String message;

    Messages(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }

}
