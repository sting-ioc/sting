package com.example.injector.outputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;
import sting.Injector;

@Injector
interface AllPrimitiveProviderBoxedCollectionKindsOutputModel
{
  Collection<Boolean> getValue1s();

  Collection<Supplier<Boolean>> getValue1Suppliers();

  Collection<Character> getValue2s();

  Collection<Supplier<Character>> getValue2Suppliers();

  Collection<Byte> getValue3s();

  Collection<Supplier<Byte>> getValue3Suppliers();

  Collection<Short> getValue4s();

  Collection<Supplier<Short>> getValue4Suppliers();

  Collection<Integer> getValue5s();

  Collection<Supplier<Integer>> getValue5Suppliers();

  Collection<Long> getValue6s();

  Collection<Supplier<Long>> getValue6Suppliers();

  Collection<Float> getValue7s();

  Collection<Supplier<Float>> getValue7Suppliers();

  Collection<Double> getValue8s();

  Collection<Supplier<Double>> getValue8Suppliers();

  @Fragment
  interface MyFragment
  {
    default boolean provideValue1()
    {
      return true;
    }

    default char provideValue2()
    {
      return 'W';
    }

    default byte provideValue3()
    {
      return 33;
    }

    default short provideValue4()
    {
      return 34;
    }

    default int provideValue5()
    {
      return 35;
    }

    default long provideValue6()
    {
      return 36L;
    }

    default float provideValue7()
    {
      return 37.5F;
    }

    default double provideValue8()
    {
      return 38.5D;
    }
  }
}
