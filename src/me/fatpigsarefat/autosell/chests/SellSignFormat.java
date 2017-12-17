package me.fatpigsarefat.autosell.chests;

import me.fatpigsarefat.autosell.AutoSell;

import java.util.List;

public class SellSignFormat {

    private static String signTrigger;
    private static List<String> autosellCooldown;
    private static List<String> triggersellStandby;
    private static List<String> triggersellCooldown;
    private static List<String> sellchestDisable;
    private static List<String> noPermission;
    private static List<String> notAChest;
    private static List<String> limitReached;
    private static List<String> economyFail;
    private static List<String> tooPoor;

    public SellSignFormat() {
        signTrigger = AutoSell.getInstance().getConfig().getString("sign-layout.trigger");
        autosellCooldown = AutoSell.getInstance().getConfig().getStringList("sign-layout.autosell");
        triggersellStandby = AutoSell.getInstance().getConfig().getStringList("sign-layout.rightclicksell.ready");
        triggersellCooldown = AutoSell.getInstance().getConfig().getStringList("sign-layout.rightclicksell.cooldown");
        sellchestDisable = AutoSell.getInstance().getConfig().getStringList("sign-layout.sellchestdisable");
        noPermission = AutoSell.getInstance().getConfig().getStringList("sign-layout.nopermission");
        notAChest = AutoSell.getInstance().getConfig().getStringList("sign-layout.notachest");
        limitReached = AutoSell.getInstance().getConfig().getStringList("sign-layout.limitreached");
        economyFail = AutoSell.getInstance().getConfig().getStringList("sign-layout.ecofail");
        tooPoor = AutoSell.getInstance().getConfig().getStringList("sign-layout.toopoor");
    }

    public static List<String> getAutosellCooldown() {
        return autosellCooldown;
    }

    public static List<String> getTriggersellStandby() {
        return triggersellStandby;
    }

    public static List<String> getTriggersellCooldown() {
        return triggersellCooldown;
    }

    public static String getSignTrigger() {
        return signTrigger;
    }

    public static List<String> getSellchestDisable() {
        return sellchestDisable;
    }

    public static List<String> getNoPermission() {
        return noPermission;
    }

    public static List<String> getLimitReached() {
        return limitReached;
    }

    public static List<String> getEconomyFail() {
        return economyFail;
    }

    public static List<String> getTooPoor() {
        return tooPoor;
    }
}
