package com.example.provider_backed.named;

import sting.Named;
import sting.StingProvider;

public final class NamedOnProviderBackedTypeModel
{
  @StingProvider( "[SimpleName]Impl" )
  @interface FrameworkComponent
  {
  }

  @FrameworkComponent
  @Named( "value" )
  static class MyComponent
  {
  }
}
