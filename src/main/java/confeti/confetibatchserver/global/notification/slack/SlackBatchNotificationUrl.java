package confeti.confetibatchserver.global.notification.slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(
    value = {"prod"}
)
public class SlackBatchNotificationUrl implements SlackNotificationUrl {

    @Value("${notification.slack.batch.url}")
    private String webhookUrl;

    @Override
    public SlackNotificationType getType() {
        return SlackNotificationType.JOB_FAILED;
    }

    @Override
    public String getUrl() {
        return webhookUrl;
    }
}
