package com.example.injector.includes.provider.naming.flat_enclosing;

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
    }
  }
}
