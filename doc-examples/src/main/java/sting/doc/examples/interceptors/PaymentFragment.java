package sting.doc.examples.interceptors;

import sting.Fragment;
import sting.Typed;

@Fragment
public interface PaymentFragment {
    @Timed
    @Typed(PaymentGateway.class)
    default PaymentGateway providePaymentGateway() {
        return (accountId, amount) -> {};
    }
}
