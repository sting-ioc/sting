package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.After;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class UnannotatedOverrideLifecycleModel
{
  private UnannotatedOverrideLifecycleModel()
  {
  }

  @Trace
  interface Service
  {
  }

  @Injectable
  @Typed( Service.class )
  static class Model
    implements Service
  {
  }

  public static class BaseInterceptor
  {
    @Before
    public void before()
    {
    }
  }

  @Injectable
  public static class TraceInterceptor
    extends BaseInterceptor
  {
    public void before()
    {
    }

    @After
    public void after()
    {
    }
  }

  @Injector( includes = Model.class, fragmentOnly = false )
  interface MyInjector
  {
    Service service();
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.UnannotatedOverrideLifecycleModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
  }
}
