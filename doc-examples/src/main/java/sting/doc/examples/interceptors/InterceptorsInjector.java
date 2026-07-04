package sting.doc.examples.interceptors;

import javax.annotation.Nonnull;
import sting.Injector;

@Injector(
        fragmentOnly = false,
        includes = {AccountServiceImpl.class, PaymentFragment.class, NotificationFragment.class})
public interface InterceptorsInjector {
    @Nonnull
    static InterceptorsInjector create() {
        return new Sting_InterceptorsInjector();
    }

    @Nonnull
    AccountService accountService();

    @Nonnull
    PaymentGateway paymentGateway();

    @Nonnull
    NotificationSender notificationSender();

    @Nonnull
    NotificationAuditTrail notificationAuditTrail();
}
