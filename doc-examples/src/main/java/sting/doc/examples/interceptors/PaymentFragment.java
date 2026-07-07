package sting.doc.examples.interceptors;

import sting.Fragment;

@Fragment
public interface PaymentFragment {
    @Timed
    default PaymentGateway providePaymentGateway() {
        return (accountId, amount) -> {};
    }
}
