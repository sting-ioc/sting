package com.example.injector.outputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface WildcardSupplierCollectionOutputModel
{
  Collection<Supplier<?>> getMyThing();
}
