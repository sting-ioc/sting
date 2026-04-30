package com.example.provider_backed.named;

import sting.ActAsStingProvider;
import sting.Named;

public final class NamedOnActAsStingProviderBackedTypeModel
{
  @ActAsStingProvider
  @interface FrameworkComponent
  {
  }

  @FrameworkComponent
  @Named( "value" )
  static class MyComponent
  {
  }
}
