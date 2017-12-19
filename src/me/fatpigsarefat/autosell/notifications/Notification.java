package me.fatpigsarefat.autosell.notifications;

public class Notification {

    private double amount;
    private NotificationType notificationType;

    public Notification(double amount, NotificationType notificationType) {
        this.amount = amount;
        this.notificationType = notificationType;
    }

    public double getAmount() {
        return amount;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }
}
