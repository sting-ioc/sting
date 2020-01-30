package com.example.fragment.inputs;

import java.util.function.Consumer;
import sting.Fragment;

@Fragment
public interface RawParameterizedInputModel
{
  @SuppressWarnings( "rawtypes" )
  default String provideX( Consumer consumer )
  {
    return null;
  }
}
