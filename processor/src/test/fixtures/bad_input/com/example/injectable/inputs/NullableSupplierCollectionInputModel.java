package com.example.injectable.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Injectable;

@Injectable
public class NullableSupplierCollectionInputModel
{
  NullableSupplierCollectionInputModel( @Nullable Collection<Supplier<String>> someValue )
  {
  }
}
