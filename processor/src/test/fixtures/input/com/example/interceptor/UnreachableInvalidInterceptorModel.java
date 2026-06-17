package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Fragment;
import sting.Injector;
import sting.interceptors.InterceptorBinding;

public final class UnreachableInvalidInterceptorModel
{
  private UnreachableInvalidInterceptorModel()
  {
  }

  interface Service
  {
    void run();
  }

  static class Model
    implements Service
  {
    public void run()
    {
    }
  }

  @InvalidTrace
  interface UnusedService
  {
    void unused();
  }

  static class UnusedModel
    implements UnusedService
  {
    public void unused()
    {
    }
  }

  @Fragment
  interface MyFragment
  {
    default Service service()
    {
      return new Model();
    }

    default UnusedService unused()
    {
      return new UnusedModel();
    }
  }

  @Injector( includes = MyFragment.class )
  interface MyInjector
  {
    Service service();
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.UnreachableInvalidInterceptorModel.MissingInterceptor",
                       priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface InvalidTrace
  {
  }
}
