package sting.doc.examples.interceptors;

import sting.Fragment;
import sting.Typed;

@Fragment
public interface NotificationFragment {
    @Audited(action = "notifications")
    @Typed({NotificationSender.class, NotificationAuditTrail.class})
    default NotificationService provideNotificationService() {
        return new NotificationService();
    }
}
