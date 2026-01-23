package confeti.confetibatchserver.global.notification;

public interface NotificationAgent {

    void notify(NotificationType type, String message);
}
