package com.example.fragment.inputs;

import java.util.Collection;
import java.util.List;
import sting.Fragment;

@Fragment
public interface RawParameterizedCollectionInputModel
{
  @SuppressWarnings( "rawtypes" )
  default String provideX( Collection<List> supplier )
  {
    return null;
  }
}
