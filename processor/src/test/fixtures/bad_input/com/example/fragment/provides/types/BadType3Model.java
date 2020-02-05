package com.example.fragment.provides.types;

import sting.Fragment;
import sting.Provides;
import sting.Service;

@Fragment
public interface BadType3Model
{
  interface MyBaseInterface
  {
  }

  interface MyOuterInterface
    extends MyBaseInterface
  {
  }

  // Does not implement MyOuterInterface!
  @Provides( services = { @Service( type = MyBaseInterface.class ), @Service( type = MyOuterInterface.class ) } )
  default MyBaseInterface provideX()
  {
    return null;
  }
}
