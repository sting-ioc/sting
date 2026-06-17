package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class CombinedBindingModel
{
  private CombinedBindingModel()
  {
  }

  @Injector( includes = Model.class, fragmentOnly = false )
  interface MyInjector
  {
    Service service();
  }

  @ServiceTrace
  interface Service
  {
    void run();
  }

  @ImplementationTrace
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
  public static class ServiceTraceInterceptor
  {
    @Before
    public void before()
    {
    }
  }

  @Injectable
  public static class ImplementationTraceInterceptor
  {
    @Before
    public void before()
    {
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.CombinedBindingModel.ServiceTraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface ServiceTrace
  {
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.CombinedBindingModel.ImplementationTraceInterceptor", priority = 200 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface ImplementationTrace
  {
  }
}
