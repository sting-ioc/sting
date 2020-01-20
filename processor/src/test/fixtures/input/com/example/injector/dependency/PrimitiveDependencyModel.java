package com.example.injector.dependency;

import sting.Fragment;
import sting.Injector;

@Injector( includes = PrimitiveDependencyModel.MyFragment.class )
abstract class PrimitiveDependencyModel
{
  abstract boolean getValue1();

  abstract char getValue2();

  abstract byte getValue3();

  abstract short getValue4();

  abstract int getValue5();

  abstract long getValue6();

  abstract float getValue7();

  abstract double getValue8();

  @Fragment
  public interface MyFragment
  {
    default boolean provideValue()
    {
      return false;
    }

    default char provideValue2()
    {
      return 0;
    }

    default byte provideValue3()
    {
      return 0;
    }

    default short provideValue4()
    {
      return 0;
    }

    default int provideValue5()
    {
      return 0;
    }

    default long provideValue6()
    {
      return 0;
    }

    default float provideValue7()
    {
      return 0;
    }

    default double provideValue8()
    {
      return 0;
    }
  }
}
