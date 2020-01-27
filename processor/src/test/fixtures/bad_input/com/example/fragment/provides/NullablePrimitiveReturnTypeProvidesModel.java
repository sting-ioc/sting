package com.example.fragment.provides;

import javax.annotation.Nullable;
import sting.Fragment;

@Fragment
public interface NullablePrimitiveReturnTypeProvidesModel
{
  @SuppressWarnings( "NullableProblems" )
  @Nullable
  default int provideX()
  {
    return 0;
  }
}
