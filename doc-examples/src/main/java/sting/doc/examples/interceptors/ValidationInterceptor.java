package sting.doc.examples.interceptors;

import sting.Injectable;
import sting.interceptors.Arguments;
import sting.interceptors.Around;
import sting.interceptors.Proceed;
import sting.interceptors.Invocation;

@Injectable
public final class ValidationInterceptor
{
  @Around
  public Object around( @Proceed final Invocation invocation, @Arguments final Object[] arguments )
    throws Throwable
  {
    if ( arguments.length > 1 && arguments[ 1 ] instanceof Integer && (Integer) arguments[ 1 ] <= 0 )
    {
      throw new IllegalArgumentException( "amount must be positive" );
    }
    return invocation.proceed();
  }
}
