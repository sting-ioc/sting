package sting.doc.examples.interceptors;

import org.jspecify.annotations.NullMarked;
import sting.Injector;

@Injector(
        fragmentOnly = false,
        includes = {AccountServiceImpl.class, PaymentFragment.class, NotificationFragment.class})
@NullMarked
public interface InterceptorsInjector {
    static InterceptorsInjector create() {
        return new Sting_InterceptorsInjector();
    }

    AccountService accountService();

    PaymentGateway paymentGateway();

    NotificationSender notificationSender();

    NotificationAuditTrail notificationAuditTrail();
}
