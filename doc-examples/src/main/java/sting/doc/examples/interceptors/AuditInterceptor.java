package sting.doc.examples.interceptors;

import sting.Injectable;
import sting.interceptors.Arguments;
import sting.interceptors.Before;
import sting.interceptors.BindingValue;

@Injectable
public final class AuditInterceptor
{
  @Before
  public void before( @BindingValue( "action" ) final String action, @Arguments final Object[] arguments )
  {
  }
}
