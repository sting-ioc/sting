package com.example.injector;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface AllPrimitiveProviderBoxedDependencyModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
    MyModel( final Boolean value1,
             final Character value2,
             final Byte value3,
             final Short value4,
             final Integer value5,
             final Long value6,
             final Float value7,
             final Double value8 )
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
      return 'Z';
    }

    default byte provideValue3()
    {
      return 3;
    }

    default short provideValue4()
    {
      return 4;
    }

    default int provideValue5()
    {
      return 5;
    }

    default long provideValue6()
    {
      return 6L;
    }

    default float provideValue7()
    {
      return 7.5F;
    }

    default double provideValue8()
    {
      return 8.5D;
    }
  }
}
