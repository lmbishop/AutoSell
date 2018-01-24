package me.fatpigsarefat.autosell.chests;

import me.fatpigsarefat.autosell.AutoSell;
import me.fatpigsarefat.autosell.events.bukkitevents.AutoSellChestSellEvent;
import me.fatpigsarefat.autosell.notifications.Notification;
import me.fatpigsarefat.autosell.notifications.NotificationType;
import me.fatpigsarefat.autosell.utils.Config;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.*;

public class SellChest {

    private int cooldown;
    private Location chestLocation;
    private Location signLocation;
    private UUID owner;
    private List<UUID> members;

    public SellChest(int cooldown, Location chestLocation, Location signLocation, UUID owner, List<UUID> members) {
        this.cooldown = cooldown;
        this.chestLocation = chestLocation;
        this.signLocation = signLocation;
        this.owner = owner;
        this.members = members;
    }

    public UUID getOwner() {
        return owner;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public Location getChestLocation() {
        return chestLocation;
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public void updateSign() {
        if (!getSignLocation().getChunk().isLoaded()) {
            return;
        }
        if (getSignLocation().getBlock().getType() == Material.SIGN_POST || getSignLocation().getBlock().getType() == Material.WALL_SIGN || getSignLocation().getBlock()
                .getType() == Material.SIGN) {
            Sign sign = (Sign) getSignLocation().getBlock().getState();
            List<String> lines;

            if (Config.sellchestMode.equalsIgnoreCase("RIGHTCLICKSELL")) {
                if (cooldown <= 0) {
                    lines = SellSignFormat.getTriggersellStandby();
                } else {
                    lines = SellSignFormat.getTriggersellCooldown();
                }
            } else {
                lines = SellSignFormat.getAutosellCooldown();
            }

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("<username>", Bukkit.getOfflinePlayer(getOwner()).getName());
            placeholders.put("<seconds>", String.valueOf(getCooldown()));
            List<String> coloredLines = new ArrayList<>();
            for (String s : lines) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    String placeholder = entry.getKey();
                    String value = entry.getValue();

                    s = s.replace(placeholder, value);
                }
                coloredLines.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            while (coloredLines.size() < 4) {
                coloredLines.add("");
            }

            sign.setLine(0, coloredLines.get(0));
            sign.setLine(1, coloredLines.get(1));
            sign.setLine(2, coloredLines.get(2));
            sign.setLine(3, coloredLines.get(3));
            sign.update();
        }
    }

    public String getOwnersName() {
        return Bukkit.getOfflinePlayer(getOwner()).getName();
    }

    public boolean addMember(UUID uuid) {
        if (!members.contains(uuid)) {
            members.add(uuid);
            return true;
        }
        return false;
    }

    public boolean addMember(Player player) {
        if (addMember(player.getUniqueId())) {
            //player.sendMessage(ChatColor.GREEN + "You are now a member of " + getOwnersName() + " sell chest at " + getChestLocation().getBlockX() + ", " + getChestLocation().getBlockY() + ", " + getChestLocation().getBlockZ());
            return true;
        }
        return false;
    }

    public boolean removeMember(UUID uuid) {
        if (members.contains(uuid)) {
            members.remove(uuid);
            return true;
        }
        return false;
    }

    public boolean removeMember(Player player) {
        if (removeMember(player.getUniqueId())) {
            //player.sendMessage(ChatColor.GREEN + "You are no longer a member of " + getOwnersName() + " sell chest at " + getChestLocation().getBlockX() + ", " + getChestLocation().getBlockY() + ", " + getChestLocation().getBlockZ());
            return true;
        }
        return false;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void decrementCooldown() {
        cooldown = cooldown - 1;
    }

    public void resetCooldown() {
        this.cooldown = AutoSell.getInstance().getConfig().getInt("sell-timer");
    }

    public void executeSale() {
        if (!getChestLocation().getChunk().isLoaded()) {
            return;
        }

        OfflinePlayer op = Bukkit.getOfflinePlayer(getOwner());
        if (getChestLocation().getBlock().getType() == Material.CHEST || getChestLocation().getBlock().getType() == Material.TRAPPED_CHEST) {
            Chest chest = (Chest) getChestLocation().getBlock().getState();
            double totalSale = 0.0;
            int slot = 0;
            NumberFormat numf = NumberFormat.getInstance();
            numf.setGroupingUsed(true);
            HashMap<Material, Integer> itemAmounts = new HashMap<>();
            HashMap<Material, Double> itemPrices = new HashMap<>();
            ArrayList<Integer> slots = new ArrayList<>();
            for (ItemStack ischest : chest.getInventory()) {
                if (ischest == null || ischest.getType().equals(Material.AIR) || ischest.getType() == null) {
                    slot++;
                    continue;
                }
                String type = ischest.getType().toString().toLowerCase();
                type = type.replace("_", "");
                String dataCode = String.valueOf(ischest.getData().getData());
                String key;
                if (AutoSell.getPrices().containsKey(type)) {
                    key = type;
                } else if (AutoSell.getPrices().containsKey(type + ":" + dataCode)) {
                    key = type + ":" + dataCode;
                } else {
                    continue;
                }

                slots.add(slot);
                totalSale += (AutoSell.getPrices().get(key) * ischest.getAmount() * Config.priceMultiplier);
                if (itemPrices.containsKey(ischest.getType())) {
                    itemPrices.put(ischest.getType(),
                            itemPrices.get(ischest.getType()) + (AutoSell.getPrices().get(key)
                                    * ischest.getAmount() * Config.priceMultiplier));
                } else {
                    itemPrices.put(ischest.getType(), (AutoSell.getPrices().get(key) * ischest.getAmount()
                            * Config.priceMultiplier));
                }
                if (itemAmounts.containsKey(ischest.getType())) {
                    itemAmounts.put(ischest.getType(),
                            itemAmounts.get(ischest.getType()) + ischest.getAmount());
                } else {
                    itemAmounts.put(ischest.getType(), ischest.getAmount());
                }
                slot++;
            }
            AutoSellChestSellEvent autoSellChestSellEvent = new AutoSellChestSellEvent(this, totalSale);
            Bukkit.getPluginManager().callEvent(autoSellChestSellEvent);
            if (!autoSellChestSellEvent.isCancelled()) {
                for (int s : slots) {
                    chest.getInventory().setItem(s, new ItemStack(Material.AIR));
                }
                List<UUID> uuids = new ArrayList<>();
                uuids.add(getOwner());
                uuids.addAll(getMembers());
                double splitAmounts = autoSellChestSellEvent.getSellAmount() / uuids.size();
                for (UUID uuid : uuids) {
                    double boostedAmount = splitAmounts;
                    OfflinePlayer sellPlayer = Bukkit.getOfflinePlayer(uuid);
                    double multiplyAmount = 1;
                    if (sellPlayer.isOnline()) {
                        multiplyAmount = multiplyAmount + (AutoSell.getPlayerManager().getPlayer(Bukkit.getPlayer(sellPlayer.getUniqueId())).getBooster() / 100D);
                    }
                    boostedAmount = boostedAmount * multiplyAmount;
                    EconomyResponse er = AutoSell.getEconomy().depositPlayer(sellPlayer, boostedAmount);
                    if (er.transactionSuccess()) {
                        if (sellPlayer.isOnline()) {
                            NotificationType notificationType;
                            if (getOwner().equals(uuid)) {
                                notificationType = NotificationType.OWNED_CHEST;
                            } else {
                                notificationType = NotificationType.SHARED_CHEST;
                            }
                            if (Config.sellchestMode.equalsIgnoreCase("AUTOSELL")) {
                                Notification notification = new Notification(boostedAmount, notificationType);
                                AutoSell.getNotificationDispatcher().addPendingNotificaion(uuid, notification);
                            } else if (Config.sellchestMode.equalsIgnoreCase("RIGHTCLICKSELL")) {
                                Notification notification = new Notification(boostedAmount, notificationType);
                                AutoSell.getNotificationDispatcher().dispatchNotifications(uuid, notification);
                            }
                        }
                    }
                }
            }
        } else {
            // System.out.println("[AutoSell] Transaction has failed for Chest Sale");
        }
    }
}
