package com.example.injector.includes.provider.naming.compound;

import sting.Injectable;
import sting.Typed;

public interface Outer
{
  class Middle
  {
    public static class Leaf
    {
      @MyFrameworkComponent
      public static class MyModel2
      {
      }

      @Injectable
      @Typed( MyModel2.class )
      public static class MyModel2Impl
        extends MyModel2
      {
      }
    }
  }
}
