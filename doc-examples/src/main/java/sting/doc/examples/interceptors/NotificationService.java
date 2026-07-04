package sting.doc.examples.interceptors;

public final class NotificationService implements NotificationSender, NotificationAuditTrail {
    @Override
    public void send(final String message) {}

    @Override
    public void record(final String message) {}
}
