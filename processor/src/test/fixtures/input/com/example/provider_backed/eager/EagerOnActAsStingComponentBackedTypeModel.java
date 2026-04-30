package com.example.provider_backed.eager;

import sting.ActAsStingComponent;
import sting.Eager;

public final class EagerOnActAsStingComponentBackedTypeModel
{
  @ActAsStingComponent
  @interface FrameworkComponent
  {
  }

  @FrameworkComponent
  @Eager
  static class MyComponent
  {
  }
}
