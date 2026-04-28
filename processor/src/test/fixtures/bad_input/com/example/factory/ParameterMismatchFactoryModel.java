package com.example.factory;

import sting.Factory;

public final class ParameterMismatchFactoryModel
{
  public static class MyComponent
  {
    MyComponent( final int count )
    {
    }
  }

  @Factory
  public interface MyComponentFactory
  {
    MyComponent create( int total );
  }
}
