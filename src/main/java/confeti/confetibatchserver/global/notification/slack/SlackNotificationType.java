package confeti.confetibatchserver.global.notification.slack;

import confeti.confetibatchserver.global.notification.NotificationType;

public enum SlackNotificationType implements NotificationType {
    JOB_FAILED,
    ;

    public String getTitle() {
        return switch (this) {
            case JOB_FAILED -> "🔴 Job Failed";
        };
    }
}
