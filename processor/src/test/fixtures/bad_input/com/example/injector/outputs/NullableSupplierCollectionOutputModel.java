package com.example.injector.outputs;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Injector;

@Injector
public interface NullableSupplierCollectionOutputModel
{
  @Nullable
  Collection<Supplier<String>> getMyThing();
}
