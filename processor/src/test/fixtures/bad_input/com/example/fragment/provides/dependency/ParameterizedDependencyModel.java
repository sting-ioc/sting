package com.example.fragment.provides.dependency;

import java.util.function.Consumer;
import sting.Fragment;

@Fragment
public interface ParameterizedDependencyModel
{
  default String provideX( Consumer<String> consumer )
  {
    return null;
  }
}
