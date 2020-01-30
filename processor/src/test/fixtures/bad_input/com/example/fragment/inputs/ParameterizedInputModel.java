package com.example.fragment.inputs;

import java.util.function.Consumer;
import sting.Fragment;

@Fragment
public interface ParameterizedInputModel
{
  default String provideX( Consumer<String> consumer )
  {
    return null;
  }
}
