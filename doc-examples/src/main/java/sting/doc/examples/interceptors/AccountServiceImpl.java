package sting.doc.examples.interceptors;

import sting.Injectable;
import sting.Typed;

@Injectable
@Typed( AccountService.class )
public final class AccountServiceImpl
  implements AccountService
{
  @Override
  public void updateAccount( final String accountId )
  {
  }
}
