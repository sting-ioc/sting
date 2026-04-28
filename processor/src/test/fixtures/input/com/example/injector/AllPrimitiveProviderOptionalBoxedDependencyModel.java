package com.example.injector;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface AllPrimitiveProviderOptionalBoxedDependencyModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
    MyModel( @Nullable final Boolean value1,
             @Nullable final Character value2,
             @Nullable final Byte value3,
             @Nullable final Short value4,
             @Nullable final Integer value5,
             @Nullable final Long value6,
             @Nullable final Float value7,
             @Nullable final Double value8 )
    {
    }
  }

  @Fragment
  interface MyFragment
  {
    default boolean provideValue1()
    {
      return true;
    }

    default char provideValue2()
    {
      return 'Y';
    }

    default byte provideValue3()
    {
      return 13;
    }

    default short provideValue4()
    {
      return 14;
    }

    default int provideValue5()
    {
      return 15;
    }

    default long provideValue6()
    {
      return 16L;
    }

    default float provideValue7()
    {
      return 17.5F;
    }

    default double provideValue8()
    {
      return 18.5D;
    }
  }
}
