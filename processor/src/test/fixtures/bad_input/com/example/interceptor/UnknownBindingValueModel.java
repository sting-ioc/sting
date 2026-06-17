package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.Before;
import sting.interceptors.BindingValue;
import sting.interceptors.InterceptorBinding;

public final class UnknownBindingValueModel
{
  private UnknownBindingValueModel()
  {
  }

  @Injector( includes = UnknownBindingValueModel.Model.class, fragmentOnly = false )
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

  @Injectable
  public static class TraceInterceptor
  {
    @Before
    public void before( @BindingValue( "missing" ) String value )
    {
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.UnknownBindingValueModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
  }
}
