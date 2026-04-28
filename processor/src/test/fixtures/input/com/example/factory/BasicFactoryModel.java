package com.example.factory;

import javax.annotation.Nonnull;
import sting.Factory;
import sting.Injectable;

public final class BasicFactoryModel
{
  @Injectable
  public static class SomeService
  {
  }

  public static class MyComponent
  {
    MyComponent( @Nonnull final SomeService someService, final int someParameter )
    {
    }
  }

  @Factory
  public interface MyComponentFactory
  {
    @Nonnull
    MyComponent create( int someParameter );

    default int increment( final int value )
    {
      return value + 1;
    }
  }
}
