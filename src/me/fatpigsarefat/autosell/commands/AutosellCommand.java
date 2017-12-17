package me.fatpigsarefat.autosell.commands;

import me.fatpigsarefat.autosell.AutoSell;
import me.fatpigsarefat.autosell.chests.SellChest;
import me.fatpigsarefat.autosell.player.ASPlayer;
import me.fatpigsarefat.autosell.utils.Config;
import me.fatpigsarefat.autosell.utils.Messages;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutosellCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            ASPlayer asPlayer = AutoSell.getPlayerManager().getPlayer((Player) sender);
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GREEN + "/autosell notify");
                sender.sendMessage(ChatColor.GREEN + "/autosell list");
                sender.sendMessage(ChatColor.GREEN + "/autosell addmember <id|all> <player>");
                sender.sendMessage(ChatColor.GREEN + "/autosell removemember <id|all> <player>");
                sender.sendMessage(ChatColor.GREEN + "/autosell migratedata");
            } else if (args[0].equalsIgnoreCase("migratedata")) {
                File f = new File(AutoSell.getInstance().getDataFolder() + File.separator + "data.yml");
                if (f.exists()) {
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
                    int migrated = 0;
                    if (yaml.contains("autosell")) {
                        for (String s : yaml.getConfigurationSection("autosell").getKeys(false)) {
                            String[] parts = s.split(", ");
                            String owner = yaml.getString("autosell." + s + ".owner");
                            Location locChest = new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]),
                                    Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

                            String sign = yaml.getString("autosell." + s + ".sign");
                            String[] signParts = sign.split(", ");
                            Location locSign = new Location(Bukkit.getWorld(signParts[0]), Integer.parseInt(signParts[1]),
                                    Integer.parseInt(signParts[2]), Integer.parseInt(signParts[3]));

                            SellChest sellChest = new SellChest(Config.sellTimer, locChest, locSign, UUID.fromString(owner), new ArrayList<>());
                            AutoSell.getSellChestManager().registerSellChest(sellChest);
                            migrated++;
                        }
                    }
                    sender.sendMessage(ChatColor.GREEN + "Successfully loaded " + migrated + " chests from old data files. These chests will be saved using " +
                            "the new " +
                            "system when the server is shut down safely.");
                } else {
                    sender.sendMessage(ChatColor.RED + "There is no data to migrate.");
                }
            } else if (args[0].equalsIgnoreCase("notify")) {
                if (asPlayer.isSubscribedToNotifications()) {
                    sender.sendMessage(Messages.AUTOSELL_NOTIFICATIONS_OFF.getMessage());
                } else {
                    sender.sendMessage(Messages.AUTOSELL_NOTIFICATIONS_ON.getMessage());
                }
                asPlayer.setSubscribedToNotifications(!asPlayer.isSubscribedToNotifications());
            } else if (args[0].equalsIgnoreCase("addmember")) {
                if (args.length >= 3) {
                    if (StringUtils.isNumeric(args[1])) {
                        int n = Integer.parseInt(args[1]);
                        SellChest sellChest = null;
                        if (n <= asPlayer.getOwnedSellChests().size()) {
                            sellChest = asPlayer.getOwnedSellChests().get(n - 1);
                        }
                        if (!(sellChest == null)) {
                            if (Bukkit.getPlayer(args[2]) != null) {
                                if (sellChest.addMember(Bukkit.getPlayer(args[2]))) {
                                    sender.sendMessage(ChatColor.GREEN + "Successfully added " + Bukkit.getPlayer(args[2]).getName() + " to your chest.");
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + Bukkit.getPlayer(args[2]).getName() + " is already a member of your chest.");
                                }
                            } else {
                                sender.sendMessage(ChatColor.GREEN + args[2] + " is not online.");
                            }
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Chest does not exist.");
                        }
                    } else if (args[1].equalsIgnoreCase("all")) {
                        if (Bukkit.getPlayer(args[2]) != null) {
                            for (SellChest sellChest : asPlayer.getOwnedSellChests()) {
                                if (sellChest.addMember(Bukkit.getPlayer(args[2]))) {
                                    sender.sendMessage(ChatColor.GREEN + "Successfully added " + Bukkit.getPlayer(args[2]).getName() + " to your chest.");
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + Bukkit.getPlayer(args[2]).getName() + " is already a member of your chest.");
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.GREEN + args[2] + " is not online.");
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("removemember")) {
                if (args.length >= 3) {
                    if (StringUtils.isNumeric(args[1])) {
                        int n = Integer.parseInt(args[1]);
                        SellChest sellChest = null;
                        if (n <= asPlayer.getOwnedSellChests().size()) {
                            sellChest = asPlayer.getOwnedSellChests().get(n - 1);
                        }
                        if (!(sellChest == null)) {
                            if (Bukkit.getOfflinePlayer(args[2]) != null) {
                                if (sellChest.removeMember(Bukkit.getOfflinePlayer(args[2]).getUniqueId())) {
                                    sender.sendMessage(ChatColor.GREEN + "Successfully removed " + Bukkit.getOfflinePlayer(args[2]).getName() + " from your " +
                                            "chest.");
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + Bukkit.getOfflinePlayer(args[2]).getName() + " was never a member of your chest.");
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Chest does not exist.");
                        }
                    } else if (args[1].equalsIgnoreCase("all")) {
                        if (Bukkit.getOfflinePlayer(args[2]) != null) {
                            for (SellChest sellChest : asPlayer.getOwnedSellChests()) {
                                if (sellChest.removeMember(Bukkit.getOfflinePlayer(args[2]).getUniqueId())) {
                                    sender.sendMessage(ChatColor.GREEN + "Successfully removed " + Bukkit.getOfflinePlayer(args[2]).getName() + " from your " +
                                            "chest.");
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + Bukkit.getOfflinePlayer(args[2]).getName() + " was never a member of your chest.");
                                }
                            }
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                List<SellChest> sellChests = new ArrayList<>();
                sellChests.addAll(asPlayer.getOwnedSellChests());
                sellChests.addAll(asPlayer.getSharedSellChests());
                if (!sellChests.isEmpty()) {
                    sender.sendMessage(" ");
                    int iterator = 1;
                    for (SellChest sellChest : sellChests) {
                        boolean owned = asPlayer.getOwnedSellChests().contains(sellChest);
                        sender.sendMessage(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Chest @ " + ChatColor.GREEN.toString() + ChatColor.BOLD + sellChest.getChestLocation().getBlockX() + ", " + sellChest.getChestLocation()
                                .getBlockY() + ", " + sellChest.getChestLocation().getBlockZ());
                        sender.sendMessage(ChatColor.DARK_GREEN + "Relationship: " + ChatColor.GREEN + (owned ? "Owner" : "Member"));
                        StringBuilder sb = new StringBuilder();
                        sb.append("*" + Bukkit.getOfflinePlayer(sellChest.getOwner()).getName());
                        sb.append(" ");
                        for (UUID uuid : sellChest.getMembers()) {
                            sb.append(Bukkit.getOfflinePlayer(uuid).getName());
                            sb.append(" ");
                        }
                        sender.sendMessage(ChatColor.DARK_GREEN + "Members: " + ChatColor.GREEN + sb.toString());
                        if (owned) {
                            sender.sendMessage(ChatColor.DARK_GREEN + "ID: " + ChatColor.GREEN + iterator);
                            iterator++;
                        }
                        sender.sendMessage(" ");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You have no chests which you own or are a member of.");
                }
            }
        }

        return true;
    }

}
