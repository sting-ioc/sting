package com.example.fragment.inputs;

import java.util.Collection;
import java.util.List;
import sting.Fragment;

@Fragment
public interface ParameterizedCollectionInputModel
{
  default String provideX( Collection<List<String>> supplier )
  {
    return null;
  }
}
