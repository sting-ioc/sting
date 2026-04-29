package com.example.provider_backed.named;

import sting.Named;
import sting.StingProvider;

public final class NamedOnProviderBackedCtorParamModel
{
  @StingProvider( "[SimpleName]Impl" )
  @interface FrameworkComponent
  {
  }

  @FrameworkComponent
  static class MyComponent
  {
    MyComponent( @Named( "value" ) final String value )
    {
    }
  }
}
