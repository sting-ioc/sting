package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.InterceptorBinding;

public final class PluginArgumentsModel
{
  private PluginArgumentsModel()
  {
  }

  @PluginTrace
  interface Service
  {
    void run( String name, int count );
  }

  @Injectable
  @Typed( Service.class )
  static class Model
    implements Service
  {
    public void run( final String name, final int count )
    {
    }
  }

  @Injector( includes = Model.class, fragmentOnly = false )
  interface MyInjector
  {
    Service service();
  }

  @InterceptorBinding( priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface PluginTrace
  {
  }
}
