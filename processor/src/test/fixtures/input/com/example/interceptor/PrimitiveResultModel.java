package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.After;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.Result;

public final class PrimitiveResultModel
{
  private PrimitiveResultModel()
  {
  }

  @Trace
  interface Service
  {
    int value();
  }

  @Injectable
  @Typed( Service.class )
  static class Model
    implements Service
  {
    public int value()
    {
      return 1;
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
    @After
    public void after( @Result final Object result )
    {
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.PrimitiveResultModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
  }
}
