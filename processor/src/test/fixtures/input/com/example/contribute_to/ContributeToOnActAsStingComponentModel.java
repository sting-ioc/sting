package com.example.contribute_to;

import sting.ActAsStingComponent;
import sting.ContributeTo;

public class ContributeToOnActAsStingComponentModel
{
  @ActAsStingComponent
  @interface MyFramework
  {
  }

  @MyFramework
  @ContributeTo( "Foo" )
  static class MyComponentType
  {
  }
}
