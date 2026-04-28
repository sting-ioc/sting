package com.example.factory;

import sting.Factory;

public final class MultipleConstructorsFactoryModel
{
  public static class MyComponent
  {
    MyComponent()
    {
    }

    MyComponent( final int count )
    {
    }
  }

  @Factory
  public interface MyComponentFactory
  {
    MyComponent create();
  }
}
