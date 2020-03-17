package sting.doc.examples.dependency_kinds;

import javax.annotation.Nullable;
import sting.Injectable;

@Injectable
public class ErrorResponder
{
  ErrorResponder( NotificationService notificationService,
                  @Nullable FaultAuditService faultAuditService )
  {
    //DOC ELIDE START
    //DOC ELIDE END
  }

  //DOC ELIDE START
  interface NotificationService
  {
  }

  interface FaultAuditService
  {
  }
  //DOC ELIDE END
}
