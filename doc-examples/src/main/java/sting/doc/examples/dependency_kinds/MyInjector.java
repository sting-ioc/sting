package sting.doc.examples.dependency_kinds;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
public interface MyInjector
{
  Supplier<NotificationService> getNotificationService();

  @Nullable
  FaultAuditService getFaultAuditService();

  Optional<FaultAuditService> getOptionalFaultAuditService();

  Supplier<Optional<FaultAuditService>> getFaultAuditServiceSupplier();

  Collection<Supplier<Optional<FaultAuditService>>> getFaultAuditServiceSuppliers();

  Collection<NotificationService> getNotificationServices();

  //DOC ELIDE START
  @Injectable
  class NotificationService
  {
  }

  @Injectable
  class FaultAuditService
  {
  }
  //DOC ELIDE END
}
