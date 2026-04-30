package com.example.provider_backed.typed;

import sting.ActAsStingComponent;
import sting.Typed;

public final class TypedOnActAsStingComponentBackedTypeModel
{
  @ActAsStingComponent
  @interface FrameworkComponent
  {
  }

  @FrameworkComponent
  @Typed( MyComponent.class )
  static class MyComponent
  {
  }
}
