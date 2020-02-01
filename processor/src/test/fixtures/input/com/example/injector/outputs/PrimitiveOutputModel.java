package com.example.injector.outputs;

import sting.Fragment;
import sting.Injector;

@Injector
interface PrimitiveOutputModel
{
  boolean getValue1();

  char getValue2();

  byte getValue3();

  short getValue4();

  int getValue5();

  long getValue6();

  float getValue7();

  double getValue8();

  @Fragment
  interface MyFragment
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
