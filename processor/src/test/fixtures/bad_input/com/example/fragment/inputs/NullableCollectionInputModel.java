package com.example.fragment.inputs;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Fragment;

@Fragment
public interface NullableCollectionInputModel
{
  default String provideX( @Nullable Collection<String> collection )
  {
    return null;
  }
}
