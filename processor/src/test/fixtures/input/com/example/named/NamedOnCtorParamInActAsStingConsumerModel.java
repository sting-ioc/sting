package com.example.named;

import sting.ActAsStingConsumer;
import sting.Named;

public class NamedOnCtorParamInActAsStingConsumerModel
{
  @ActAsStingConsumer
  @interface MyFramework
  {
  }

  @MyFramework
  static class MyComponentType
  {
    MyComponentType( @Named( "Foo" ) int i )
    {
    }
  }
}
