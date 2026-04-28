package com.example.injector;

import sting.Injectable;
import sting.Injector;
import sting.StingProvider;
import sting.Typed;

interface AutodetectProviderNestedModel
{
  @Injector
  interface MyInjector
  {
    Outer.Middle.Leaf.MyModel1 getMyModel1();
  }

  @StingProvider( "[CompoundName]Impl" )
  @interface MyFrameworkComponent
  {
  }

  interface Outer
  {
    class Middle
    {
      static class Leaf
      {
        @MyFrameworkComponent
        static class MyModel1
        {
        }

        @Injectable
        @Typed( MyModel1.class )
        static class MyModel1Impl
          extends MyModel1
        {
        }
      }
    }
  }
}
