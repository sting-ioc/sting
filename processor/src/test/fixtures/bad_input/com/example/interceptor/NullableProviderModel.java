package com.example.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import sting.interceptors.Before;
import sting.interceptors.InterceptorBinding;

public final class NullableProviderModel
{
  private NullableProviderModel()
  {
  }

  @Injector( includes = NullableProviderModel.MyFragment.class )
  interface MyInjector
  {
  java.util.Optional<Service> service();
  }

  interface Service
  {
  }

  @Fragment
  interface MyFragment
  {
    @Trace
    @Nullable
    default Service service()
    {
      return null;
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

  @InterceptorBinding( implementedBy = "com.example.interceptor.NullableProviderModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
  }
}
