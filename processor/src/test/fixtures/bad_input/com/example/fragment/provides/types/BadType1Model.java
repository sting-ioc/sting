package com.example.fragment.provides.types;

import sting.Fragment;
import sting.Provides;
import sting.Service;

@Fragment
public interface BadType1Model
{
  class Foo
  {
  }

  @Provides( services = @Service( type = Runnable.class ) )
  default Foo provideX()
  {
    return null;
  }
}
