package com.example.injector.includes.provider.naming.enclosing;

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
      public static class MyFramework_MyModel2
        extends MyModel2
      {
      }
    }
  }
}
