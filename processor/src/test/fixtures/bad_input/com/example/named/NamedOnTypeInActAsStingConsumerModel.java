package com.example.named;

import sting.ActAsStingConsumer;
import sting.Named;

public class NamedOnTypeInActAsStingConsumerModel
{
  @ActAsStingConsumer
  @interface MyFramework
  {
  }

  @MyFramework
  @Named( "Foo" )
  static class MyComponentType
  {
  }
}
