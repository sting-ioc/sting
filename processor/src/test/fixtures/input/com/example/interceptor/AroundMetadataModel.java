package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Arguments;
import sting.interceptors.Around;
import sting.interceptors.BindingValue;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.MethodName;
import sting.interceptors.Proceed;
import sting.interceptors.Invocation;
import sting.interceptors.ServiceType;

public final class AroundMetadataModel
{
  private AroundMetadataModel()
  {
  }

  @Trace( value = "metadata", level = 7 )
  interface Service
  {
    String run( String value, int count );
  }

  @Injectable
  @Typed( Service.class )
  static class Model
    implements Service
  {
    public String run( final String value, final int count )
    {
      return value + ":" + count;
    }
  }

  @Injector( includes = Model.class, fragmentOnly = false )
  interface MyInjector
  {
    Service service();
  }

  @Injectable
  public static class TraceInterceptor
  {
    @Around
    public Object around( @Proceed final Invocation invocation,
                          @ServiceType final String serviceType,
                          @MethodName final String methodName,
                          @BindingValue( "value" ) final String value,
                          @BindingValue( "level" ) final int level,
                          @Arguments final Object[] arguments )
      throws Throwable
    {
      return invocation.proceed();
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.AroundMetadataModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
    String value();

    int level();
  }
}
