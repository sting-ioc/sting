package com.example.provider_backed.typed;

import sting.StingProvider;
import sting.Typed;

public final class TypedOnProviderBackedTypeModel
{
  @StingProvider( "[SimpleName]Impl" )
  @interface FrameworkComponent
  {
  }

  @FrameworkComponent
  @Typed( MyComponent.class )
  static class MyComponent
  {
  }
}
