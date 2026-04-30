package com.example.provider_backed.eager;

import sting.ActAsStingProvider;
import sting.Eager;

public final class EagerOnActAsStingProviderBackedTypeModel
{
  @ActAsStingProvider
  @interface FrameworkComponent
  {
  }

  @FrameworkComponent
  @Eager
  static class MyComponent
  {
  }
}
