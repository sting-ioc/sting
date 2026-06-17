package sting.doc.examples.interceptors;

public interface PaymentGateway
{
  void charge( String accountId, int amount );
}
