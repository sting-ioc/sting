package com.example.injector.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Injector;

@Injector
public interface NullableSupplierCollectionDependencyModel
{
  @Nullable
  Collection<Supplier<String>> getMyThing();
}
