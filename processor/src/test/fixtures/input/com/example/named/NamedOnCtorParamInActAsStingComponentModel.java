package com.example.named;

import sting.ActAsStingComponent;
import sting.Named;

public class NamedOnCtorParamInActAsStingComponentModel
{
  @ActAsStingComponent
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
