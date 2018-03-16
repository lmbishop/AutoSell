package me.fatpigsarefat.autosell.events;

import me.fatpigsarefat.autosell.AutoSell;
import me.fatpigsarefat.autosell.chests.SellChest;
import me.fatpigsarefat.autosell.chests.SellSignFormat;
import me.fatpigsarefat.autosell.player.ASPlayer;
import me.fatpigsarefat.autosell.utils.Config;
import me.fatpigsarefat.autosell.utils.Messages;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignCreateEvent implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onEvent(SignChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Sign sign = (Sign) event.getBlock().getState().getData();
        Block attached = event.getBlock().getRelative(sign.getAttachedFace());
        if (event.getLine(0).equalsIgnoreCase(SellSignFormat.getSignTrigger())) {
            Player player = event.getPlayer();
            if (!Config.sellchestEnabled) {
                updateSign(event, SellSignFormat.getSellchestDisable());
                return;
            }
            if (!player.hasPermission("autosell.create")) {
                updateSign(event, SellSignFormat.getNoPermission());
                return;
            }
            if (attached.getType() == Material.CHEST || attached.getType() == Material.TRAPPED_CHEST) {
                ASPlayer asPlayer = AutoSell.getPlayerManager().getPlayer(event.getPlayer());
                if (Config.limit) {
                    if (asPlayer.getOwnedSellChests().size() >= asPlayer.getMaxLimit()) {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("<limit>", String.valueOf(asPlayer.getMaxLimit()));
                        updateSign(event, SellSignFormat.getLimitReached(), placeholders);
                        return;
                    }
                }
                if (Config.signPrice > 0) {
                    if (AutoSell.getEconomy().getBalance(player.getName()) >= Config.signPrice)  {
                        EconomyResponse er = AutoSell.getEconomy().withdrawPlayer(player.getName(), Config.signPrice);
                        if (er.transactionSuccess()) {
                            event.getPlayer().sendMessage(Messages.CHEST_PURCHASE.getMessage().replace("%money%", String.valueOf(Config.signPrice)));
                        } else {
                            updateSign(event, SellSignFormat.getEconomyFail());
                        }
                    } else {
                        updateSign(event, SellSignFormat.getTooPoor());
                    }
                }

                SellChest sellChest = new SellChest(Config.sellTimer, attached.getLocation(), event.getBlock().getLocation(), player.getUniqueId(), new ArrayList<>());
                event.getPlayer().sendMessage(Messages.CHEST_CREATE.getMessage());

                if (Config.sellchestMode.equalsIgnoreCase("RIGHTCLICKSELL")) {
                    updateSign(event, SellSignFormat.getTriggersellStandby());
                } else {
                    updateSign(event, SellSignFormat.getAutosellCooldown());
                }

                //TODO save it too since chests needs to be loaded in to auto save
                AutoSell.getSellChestManager().registerSellChest(sellChest);
            }
        }
    }

    private void updateSign(SignChangeEvent event, List<String> lines, Map<String, String> placeholders) {
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

        event.setLine(0, coloredLines.get(0));
        event.setLine(1, coloredLines.get(1));
        event.setLine(2, coloredLines.get(2));
        event.setLine(3, coloredLines.get(3));
    }

    private void updateSign(SignChangeEvent event, List<String> lines) {
        updateSign(event, lines, new HashMap<>());

    }

}
