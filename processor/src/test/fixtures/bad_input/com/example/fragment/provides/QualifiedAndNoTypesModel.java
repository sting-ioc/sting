package com.example.fragment.provides;

import sting.Eager;
import sting.Fragment;
import sting.Named;
import sting.Typed;

@Fragment
public interface QualifiedAndNoTypesModel
{
  @Eager
  @Typed( {} )
  @Named( "X" )
  default String provideX()
  {
    return "";
  }
}
