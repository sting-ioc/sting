package com.example.named;

import sting.ActAsStingProvider;
import sting.Named;

public class NamedOnTypeInActAsStingProviderModel
{
  @ActAsStingProvider
  @interface MyFramework
  {
  }

  @MyFramework
  @Named( "Foo" )
  static class MyComponentType
  {
  }
}
