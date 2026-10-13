package sting.doc.examples.dependency_kinds;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;

@Injectable
@NullMarked
public class ErrorResponder {
    ErrorResponder(NotificationService notificationService, @Nullable FaultAuditService faultAuditService) {
        // DOC ELIDE START
        // DOC ELIDE END
    }

    // DOC ELIDE START
    interface NotificationService {}

    interface FaultAuditService {}
    // DOC ELIDE END
}
