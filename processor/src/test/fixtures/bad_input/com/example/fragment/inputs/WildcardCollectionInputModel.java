package com.example.fragment.inputs;

import java.util.Collection;
import sting.Fragment;

@Fragment
public interface WildcardCollectionInputModel
{
  default String provideX( Collection<?> collection )
  {
    return null;
  }
}
