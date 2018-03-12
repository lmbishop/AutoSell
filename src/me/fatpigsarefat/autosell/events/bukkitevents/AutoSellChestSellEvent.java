package me.fatpigsarefat.autosell.events.bukkitevents;

import me.fatpigsarefat.autosell.chests.SellChest;
import me.fatpigsarefat.autosell.utils.Config;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class AutoSellChestSellEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private SellChest sellChest;
    private boolean cancelled;
    private double sellAmount;
    private String chestMode;

    public AutoSellChestSellEvent(SellChest sellChest, double sellAmount) {
        this.sellChest = sellChest;
        this.cancelled = false;
        this.sellAmount = sellAmount;
        if (Config.sellchestMode.equalsIgnoreCase("RIGHTCLICKSELL")) {
            this.chestMode = "RIGHTCLICKSELL";
        } else {
            this.chestMode = "AUTOSELL";
        }
    }

    public String getChestMode() {
        return chestMode;
    }

    public double getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(double sellAmount) {
        this.sellAmount = sellAmount;
    }

    public SellChest getSellChest() {
        return sellChest;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
