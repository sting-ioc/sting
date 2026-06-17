package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.After;
import sting.interceptors.AfterException;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.Thrown;

public final class LifecycleFailureNestingModel
{
  private LifecycleFailureNestingModel()
  {
  }

  @OuterTrace
  @InnerTrace
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
  public static class OuterInterceptor
  {
    @Before
    public void before()
    {
    }

    @After
    public void after()
    {
    }

    @AfterException
    public void afterException( @Thrown final Throwable throwable )
    {
    }
  }

  @Injectable
  public static class InnerInterceptor
  {
    @Before
    public void before()
    {
    }

    @After
    public void after()
    {
    }

    @AfterException
    public void afterException( @Thrown final Throwable throwable )
    {
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.LifecycleFailureNestingModel.OuterInterceptor",
                       priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface OuterTrace
  {
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.LifecycleFailureNestingModel.InnerInterceptor",
                       priority = 200 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface InnerTrace
  {
  }
}
