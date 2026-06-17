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

public final class NonInjectableInterceptorModel
{
  private NonInjectableInterceptorModel()
  {
  }

  @Injector( includes = NonInjectableInterceptorModel.Model.class, fragmentOnly = false )
  interface MyInjector
  {
  Service service();
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

  public static class TraceInterceptor
  {
    @Before
    public void before()
    {
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.NonInjectableInterceptorModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
  }
}
