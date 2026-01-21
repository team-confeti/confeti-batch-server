package confeti.confetibatchserver.global.notification.slack;

public interface SlackNotificationUrl {

    SlackNotificationType getType();

    String getUrl();
}
