package com.example.fragment.provides.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Fragment;

@Fragment
public interface NullableSupplierCollectionDependencyModel
{
  default String provideX( @Nullable Collection<Supplier<String>> collection )
  {
    return null;
  }
}
