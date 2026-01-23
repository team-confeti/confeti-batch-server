package confeti.confetibatchserver.global.notification.local;

import confeti.confetibatchserver.global.notification.NotificationAgent;
import confeti.confetibatchserver.global.notification.NotificationType;
import confeti.confetibatchserver.global.notification.slack.SlackNotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
//@Profile(
//    value = {"local"}
//)
public class LocalNotificationAgent implements NotificationAgent {

    @Override
    public void notify(NotificationType type, String message) {
        if (type instanceof SlackNotificationType slackNotificationType) {
            log.error("NotificationType: {}, Message: {} ", slackNotificationType.getTitle(),
                message);
            return;
        }
        log.error(message);
    }
}