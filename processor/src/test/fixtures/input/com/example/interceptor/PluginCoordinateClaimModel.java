package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Named;
import sting.Typed;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class PluginCoordinateClaimModel
{
  private PluginCoordinateClaimModel()
  {
  }

  interface Service
  {
    void run();
  }

  @PluginTrace
  @Named( "left" )
  @Injectable
  @Typed( Service.class )
  static class LeftModel
    implements Service
  {
    public void run()
    {
    }
  }

  @PluginTrace
  @Named( "right" )
  @Injectable
  @Typed( Service.class )
  static class RightModel
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

  @Injector( includes = { LeftModel.class, RightModel.class }, fragmentOnly = false )
  interface MyInjector
  {
    @Named( "left" )
    Service left();

    @Named( "right" )
    Service right();
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.PluginCoordinateClaimModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface PluginTrace
  {
  }
}
