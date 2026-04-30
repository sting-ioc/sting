package com.example.provider_backed.named;

import sting.Named;

public final class NamedOnCtorParamInThirdPartyActAsStingConsumerBackedTypeModel
{
  @interface ActAsStingConsumer
  {
  }

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
