package com.example.provider_backed.typed;

import sting.ActAsStingProvider;
import sting.Typed;

public final class TypedOnActAsStingProviderBackedTypeModel
{
  @ActAsStingProvider
  @interface FrameworkComponent
  {
  }

  @FrameworkComponent
  @Typed( MyComponent.class )
  static class MyComponent
  {
  }
}
