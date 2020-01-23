package com.example.injector.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface WildcardSupplierCollectionDependencyModel
{
  Collection<Supplier<?>> getMyThing();
}
