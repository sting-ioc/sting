package com.example.fragment.provides;

import sting.Fragment;
import sting.Provides;
import sting.Service;

@Fragment
public interface IncompatibleReturnTypeProvidesModel
{
  @Provides( services = @Service( type = Boolean.class ) )
  default String provideX()
  {
    return null;
  }
}
