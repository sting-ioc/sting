package com.example.injector.outputs;

import sting.Fragment;
import sting.Injector;

@Injector
interface AllBoxedProviderPrimitiveOutputModel
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
    default Boolean provideValue1()
    {
      return true;
    }

    default Character provideValue2()
    {
      return 'X';
    }

    default Byte provideValue3()
    {
      return 23;
    }

    default Short provideValue4()
    {
      return 24;
    }

    default Integer provideValue5()
    {
      return 25;
    }

    default Long provideValue6()
    {
      return 26L;
    }

    default Float provideValue7()
    {
      return 27.5F;
    }

    default Double provideValue8()
    {
      return 28.5D;
    }
  }
}
