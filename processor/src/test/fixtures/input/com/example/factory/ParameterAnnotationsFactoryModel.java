package com.example.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sting.Factory;
import sting.Injectable;

public final class ParameterAnnotationsFactoryModel
{
  @Injectable
  public static class SomeService
  {
  }

  public static class MyComponent
  {
    MyComponent( @Nonnull final SomeService someService,
                 @Nullable final String name,
                 @Nonnull final Runnable action,
                 final int count )
    {
    }
  }

  @Factory
  public interface MyComponentFactory
  {
    @Nonnull
    MyComponent create( @Nullable String name, @Nonnull Runnable action, int count );
  }
}
