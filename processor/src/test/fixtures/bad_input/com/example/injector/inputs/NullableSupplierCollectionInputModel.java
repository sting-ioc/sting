package com.example.injector.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Injector;

@Injector
public interface NullableSupplierCollectionInputModel
{
  @Nullable
  Collection<Supplier<String>> getMyThing();
}
