package com.example.fragment.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Fragment;

@Fragment
public interface NullableSupplierCollectionInputModel
{
  default String provideX( @Nullable Collection<Supplier<String>> collection )
  {
    return null;
  }
}
