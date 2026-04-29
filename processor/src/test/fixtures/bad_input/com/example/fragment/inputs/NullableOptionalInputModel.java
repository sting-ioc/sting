package com.example.fragment.inputs;

import java.util.Optional;
import javax.annotation.Nullable;
import sting.Fragment;

@Fragment
public interface NullableOptionalInputModel
{
  default String provideX( @Nullable Optional<String> supplier )
  {
    return "";
  }
}
