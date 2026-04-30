package com.example.provider_backed.named;

import sting.Named;

public final class NamedOnTypeInThirdPartyActAsStingProviderBackedTypeModel
{
  @interface ActAsStingProvider
  {
  }

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
