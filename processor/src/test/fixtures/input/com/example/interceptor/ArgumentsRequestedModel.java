package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Arguments;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class ArgumentsRequestedModel
{
  private ArgumentsRequestedModel()
  {
  }

  @Trace
  interface Service
  {
    void run( String value );
  }

  @Injectable
  @Typed( Service.class )
  static class Model
    implements Service
  {
    public void run( final String value )
    {
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
    @Before
    public void before( @Arguments final Object[] arguments )
    {
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.ArgumentsRequestedModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
  }
}
