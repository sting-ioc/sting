package sting.doc.examples.interceptors;

@Audited( action = "accounts" )
@Timed
public interface AccountService
{
  void updateAccount( String accountId );
}
