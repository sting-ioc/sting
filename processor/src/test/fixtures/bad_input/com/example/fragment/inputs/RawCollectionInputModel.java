package com.example.fragment.inputs;

import java.util.Collection;
import sting.Fragment;

@Fragment
public interface RawCollectionInputModel
{
  @SuppressWarnings( "rawtypes" )
  default String provideX( Collection collection )
  {
    return null;
  }
}
