package sting.doc.examples.interceptors;

@Validated
public interface PaymentGateway
{
  void charge( String accountId, int amount );
}
