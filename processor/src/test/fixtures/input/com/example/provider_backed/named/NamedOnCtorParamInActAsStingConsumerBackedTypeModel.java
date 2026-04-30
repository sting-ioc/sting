package com.example.provider_backed.named;

import sting.ActAsStingConsumer;
import sting.Named;

public final class NamedOnCtorParamInActAsStingConsumerBackedTypeModel
{
  @ActAsStingConsumer
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
