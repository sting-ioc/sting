package com.example.interceptor;

import java.io.IOException;
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

public final class BindingValueConversionsModel
{
  private BindingValueConversionsModel()
  {
  }

  enum Mode
  {
    On,
    Off
  }

  @Injector( includes = Model.class, fragmentOnly = false )
  interface MyInjector
  {
    Service service();
  }

  @Trace
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

  @Injectable
  public static class TraceInterceptor
  {
    @Before
    public void before( @BindingValue( "text" ) final String text,
                        @BindingValue( "flag" ) final boolean flag,
                        @BindingValue( "byteValue" ) final byte byteValue,
                        @BindingValue( "shortValue" ) final Short shortValue,
                        @BindingValue( "count" ) final int count,
                        @BindingValue( "longValue" ) final Long longValue,
                        @BindingValue( "floatValue" ) final float floatValue,
                        @BindingValue( "doubleValue" ) final Double doubleValue,
                        @BindingValue( "charValue" ) final char charValue,
                        @BindingValue( "mode" ) final String mode,
                        @BindingValue( "type" ) final String type,
                        @BindingValue( "texts" ) final String[] texts,
                        @BindingValue( "flags" ) final boolean[] flags,
                        @BindingValue( "bytes" ) final byte[] bytes,
                        @BindingValue( "shorts" ) final short[] shorts,
                        @BindingValue( "counts" ) final int[] counts,
                        @BindingValue( "longs" ) final long[] longs,
                        @BindingValue( "floats" ) final float[] floats,
                        @BindingValue( "doubles" ) final double[] doubles,
                        @BindingValue( "chars" ) final char[] chars,
                        @BindingValue( "modes" ) final String[] modes,
                        @BindingValue( "rollbackOn" ) final String[] rollbackOn,
                        @BindingValue( "dontRollbackOn" ) final String[] dontRollbackOn )
    {
    }
  }

  @InterceptorBinding( implementedBy = "com.example.interceptor.BindingValueConversionsModel.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  @interface Trace
  {
    String text() default "alpha";

    boolean flag() default true;

    byte byteValue() default 1;

    short shortValue() default 2;

    int count() default 3;

    long longValue() default 4L;

    float floatValue() default 5.0F;

    double doubleValue() default 6.0;

    char charValue() default '\n';

    Mode mode() default Mode.On;

    Class<?> type() default String.class;

    String[] texts() default { "alpha", "beta" };

    boolean[] flags() default { true, false };

    byte[] bytes() default { 1 };

    short[] shorts() default { 2 };

    int[] counts() default { 3 };

    long[] longs() default { 4L };

    float[] floats() default { 5.0F };

    double[] doubles() default { 6.0 };

    char[] chars() default { '\n', '\'' };

    Mode[] modes() default { Mode.On, Mode.Off };

    Class<?>[] rollbackOn() default { IOException.class };

    Class[] dontRollbackOn() default {};
  }
}
