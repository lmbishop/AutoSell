package me.fatpigsarefat.autosell.events;

import me.fatpigsarefat.autosell.AutoSell;
import me.fatpigsarefat.autosell.chests.SellChest;
import me.fatpigsarefat.autosell.utils.Config;
import me.fatpigsarefat.autosell.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakEvent implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        SellChest sellChest = null;
        for (SellChest s : AutoSell.getSellChestManager().getSellChests()) {
            if (s.getChestLocation().equals(event.getBlock().getLocation()) || s.getSignLocation().equals(event.getBlock().getLocation())) {
                sellChest = s;
                break;
            }
        }
        if (sellChest != null) {
            if (Config.sellchestProection) {
                if (!sellChest.getOwner().equals(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("autosell.admin")) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You do not own this chest and you do not have the permission autosell.admin to bypass chest protections.");
                    event.setCancelled(true);
                    return;
                } else if (event.getPlayer().hasPermission("autosell.admin")) {
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Bypassed chest protections with permission.");
                }
            }
            AutoSell.getSellChestManager().unregisterSellChest(sellChest);
            event.getPlayer().sendMessage(Messages.CHEST_DESTROY.getMessage());
        }
    }
}
