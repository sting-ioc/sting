package com.example.injectable.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Injectable;

@Injectable
public class NullableSupplierCollectionDependencyModel
{
  NullableSupplierCollectionDependencyModel( @Nullable Collection<Supplier<String>> someValue )
  {
  }
}
