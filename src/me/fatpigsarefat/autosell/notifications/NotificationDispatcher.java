package me.fatpigsarefat.autosell.notifications;

import me.fatpigsarefat.autosell.AutoSell;
import me.fatpigsarefat.autosell.player.ASPlayer;
import me.fatpigsarefat.autosell.utils.Config;
import me.fatpigsarefat.autosell.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationDispatcher {

    private HashMap<UUID, ArrayList<Notification>> pendingNotifications = new HashMap<>();

    public void addPendingNotificaion(UUID uuid, Notification notification) {
        ArrayList<Notification> notifications = new ArrayList<>();
        if (pendingNotifications.containsKey(uuid)) {
            notifications = pendingNotifications.get(uuid);
        }

        notifications.add(notification);
        pendingNotifications.put(uuid, notifications);
    }

    public void clearNotifications(UUID uuid) {
        pendingNotifications.remove(uuid);
    }

    public void dispatchNotifications() {
        for (Map.Entry<UUID, ArrayList<Notification>> entry : pendingNotifications.entrySet()) {
            UUID uuid = entry.getKey();
            ArrayList<Notification> notifications = entry.getValue();

            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                ASPlayer asPlayer = AutoSell.getPlayerManager().getPlayer(Bukkit.getPlayer(uuid));

                if (asPlayer.isSubscribedToNotifications()) {
                    ArrayList<String> messages = new ArrayList<>();
                    double total = 0.0;
                    double owned = 0.0;
                    double shared = 0.0;

                    for (Notification notification : notifications) {
                        if (notification.getNotificationType() == NotificationType.OWNED_CHEST) {
                            owned += notification.getAmount();
                        } else if (notification.getNotificationType() == NotificationType.SHARED_CHEST) {
                            shared += notification.getAmount();
                        }
                        total += notification.getAmount();
                    }

                    if (total > 0.0) {
                        messages.add(Messages.AUTOSELL_SALE_HEADER.getMessage().replace("%money%", String.valueOf(total)));
                    }
                    if (owned > 0.0) {
                        messages.add(Messages.AUTOSELL_DETAIL_OWN_CHESTS.getMessage().replace("%money%", String.valueOf(owned)));
                    }
                    if (shared > 0.0) {
                        messages.add(Messages.AUTOSELL_DETAIL_SHARED_CHESTS.getMessage().replace("%money%", String.valueOf(shared)));
                    }
                    if (!notifications.isEmpty()) {
                        double booster = asPlayer.getBooster();
                        if (booster > 0) {
                            messages.add(Messages.AUTOSELL_DETAIL_BOOSTER.getMessage().replace("%booster%", String.valueOf(booster)));
                        }
                        if (Config.priceMultiplier != 1) {
                            messages.add(Messages.AUTOSELL_DETAIL_PRICE_MULTIPLIER.getMessage().replace("%booster%", String.valueOf(Config.priceMultiplier)));
                        }
                        for (String message : messages) {
                            asPlayer.getPlayer().sendMessage(message);
                        }
                    }
                }
            }

        }

        pendingNotifications.clear();
    }

    public void startAutomaticNotifier() {
        new BukkitRunnable() {
            @Override
            public void run() {
                dispatchNotifications();
            }
        }.runTaskTimer(AutoSell.getInstance(), Config.sellTimer * 20, Config.sellTimer * 20);
    }

}
