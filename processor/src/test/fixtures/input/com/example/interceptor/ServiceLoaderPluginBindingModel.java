package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.interceptors.InterceptorBinding;

public final class ServiceLoaderPluginBindingModel
{
  private ServiceLoaderPluginBindingModel()
  {
  }

  @ServiceLoadedPluginTrace
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

  @InterceptorBinding( priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface ServiceLoadedPluginTrace
  {
  }
}
