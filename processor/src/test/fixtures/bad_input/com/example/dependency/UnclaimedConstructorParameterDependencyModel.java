package com.example.dependency;

import sting.Dependency;

public class UnclaimedConstructorParameterDependencyModel
{
  UnclaimedConstructorParameterDependencyModel( @Dependency( qualifier = "" ) String someValue )
  {
  }
}
