package com.example.deprecated;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
public interface DeprecatedProvidesDependencyNodeInjectorModel
{
  MyModel getMyModel();

  class MyModel
  {
  }

  @SuppressWarnings( "DeprecatedIsStillUsed" )
  @Injectable
  @Deprecated
  class MyOtherModel
  {
  }

  @Fragment
  interface MyFragment
  {
    default MyModel provideMyModel( MyOtherModel other )
    {
      return null;
    }
  }
}
