package com.example.provider_backed.eager;

import sting.Eager;
import sting.StingProvider;

public final class EagerOnProviderBackedTypeModel
{
  @StingProvider( "[SimpleName]Impl" )
  @interface FrameworkComponent
  {
  }

  @FrameworkComponent
  @Eager
  static class MyComponent
  {
  }
}
