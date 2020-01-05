package com.example.fragment.provides.types;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface NoTypesAndLazyModel
{
  @SuppressWarnings( "DefaultAnnotationParam" )
  @Provides( types = {}, eager = false )
  default String provideX()
  {
    return "";
  }
}
