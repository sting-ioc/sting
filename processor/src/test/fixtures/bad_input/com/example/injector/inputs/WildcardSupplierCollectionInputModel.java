package com.example.injector.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface WildcardSupplierCollectionInputModel
{
  Collection<Supplier<?>> getMyThing();
}
