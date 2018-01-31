package me.fatpigsarefat.autosell.events;

import me.fatpigsarefat.autosell.AutoSell;
import me.fatpigsarefat.autosell.chests.SellChest;
import me.fatpigsarefat.autosell.utils.Config;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class InteractEvent implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent ev) {
        if (Config.sellchestMode.equalsIgnoreCase("RIGHTCLICKSELL") && ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (SellChest sc : AutoSell.getSellChestManager().getSellChests()) {
                if (sc.getSignLocation().equals(ev.getClickedBlock().getLocation())) {
                    if (sc.getOwner().equals(ev.getPlayer().getUniqueId()) || sc.getMembers().contains(ev.getPlayer().getUniqueId())) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                ev.setCancelled(true);
                                if (sc.getCooldown() <= 0) {
                                    sc.setCooldown(Config.sellTimer);
                                    sc.executeSale();
                                    sc.updateSign();
                                }
                            }
                        }.runTaskAsynchronously(AutoSell.getInstance());
                    } else {
                        ev.getPlayer().sendMessage(ChatColor.RED + "You are not a member of this chest.");
                    }
                }
            }
        }
    }

}
