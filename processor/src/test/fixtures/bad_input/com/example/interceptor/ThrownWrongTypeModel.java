package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.AfterException;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.Thrown;

public final class ThrownWrongTypeModel
{
  private ThrownWrongTypeModel()
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

  @Injector( includes = Model.class, fragmentOnly = false )
  interface MyInjector
  {
    Service service();
  }

  @Injectable
  public static class TraceInterceptor
  {
    @AfterException
    public void afterException( @Thrown final Exception throwable )
    {
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.ThrownWrongTypeModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
  }
}
