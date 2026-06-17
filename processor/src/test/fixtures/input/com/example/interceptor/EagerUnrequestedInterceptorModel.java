package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Eager;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class EagerUnrequestedInterceptorModel
{
  private EagerUnrequestedInterceptorModel()
  {
  }

  @Trace
  interface Service
  {
    void run();
  }

  @Eager
  @Injectable
  @Typed( Service.class )
  static class Model
    implements Service
  {
    public void run()
    {
    }
  }

  @Injectable
  public static class TraceInterceptor
  {
    @Before
    public void before()
    {
    }
  }

  @Injector( includes = Model.class, fragmentOnly = false )
  interface MyInjector
  {
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.EagerUnrequestedInterceptorModel.TraceInterceptor",
                       priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
  }
}
