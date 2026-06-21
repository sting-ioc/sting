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

public final class BindingValueAnnotationArrayModel
{
  private BindingValueAnnotationArrayModel()
  {
  }

  @interface Nested
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
    @Before
    public void before( @BindingValue( "nested" ) final String[] nested )
    {
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.BindingValueAnnotationArrayModel.TraceInterceptor",
                       priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
    Nested[] nested() default {};
  }
}
