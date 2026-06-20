package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Around;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.Proceed;
import sting.interceptors.Invocation;

public final class MultipleAroundLifecycleAnnotationsModel
{
  @Trace
  interface Service
  {
    void run();
  }

  @Injectable
  @Typed( Service.class )
  static class Model
    implements Service
  {
    public void run()
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
    @Around
    public Object around( @Proceed final Invocation invocation )
      throws Throwable
    {
      return invocation.proceed();
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.MultipleAroundLifecycleAnnotationsModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
  }
}
