package com.example.fragment.provides.dependency;

import java.util.function.Consumer;
import sting.Fragment;

@Fragment
public interface RawParameterizedDependencyModel
{
  default String provideX( Consumer consumer )
  {
    return null;
  }
}
