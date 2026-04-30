package com.example.named;

import sting.ActAsStingProvider;
import sting.Named;

public class NamedOnCtorParamInActAsStingProviderModel
{
  @ActAsStingProvider
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
