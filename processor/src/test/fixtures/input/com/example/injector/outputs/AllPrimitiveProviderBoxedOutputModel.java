package com.example.injector.outputs;

import sting.Fragment;
import sting.Injector;

@Injector
interface AllPrimitiveProviderBoxedOutputModel
{
  Boolean getValue1();

  Character getValue2();

  Byte getValue3();

  Short getValue4();

  Integer getValue5();

  Long getValue6();

  Float getValue7();

  Double getValue8();

  @Fragment
  interface MyFragment
  {
    default boolean provideValue1()
    {
      return true;
    }

    default char provideValue2()
    {
      return 'V';
    }

    default byte provideValue3()
    {
      return 43;
    }

    default short provideValue4()
    {
      return 44;
    }

    default int provideValue5()
    {
      return 45;
    }

    default long provideValue6()
    {
      return 46L;
    }

    default float provideValue7()
    {
      return 47.5F;
    }

    default double provideValue8()
    {
      return 48.5D;
    }
  }
}
