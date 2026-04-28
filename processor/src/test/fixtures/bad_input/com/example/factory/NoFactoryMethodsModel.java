package com.example.factory;

import sting.Factory;

public final class NoFactoryMethodsModel
{
  @Factory
  public interface MyComponentFactory
  {
    default int increment( final int value )
    {
      return value + 1;
    }
  }
}
