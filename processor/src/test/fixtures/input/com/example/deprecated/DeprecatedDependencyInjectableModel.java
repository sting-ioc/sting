package com.example.deprecated;

import sting.Injectable;

@Injectable
public class DeprecatedDependencyInjectableModel
{
  @SuppressWarnings( "DeprecatedIsStillUsed" )
  @Deprecated
  public static class MyDep
  {
  }

  DeprecatedDependencyInjectableModel( MyDep dep )
  {
  }
}
