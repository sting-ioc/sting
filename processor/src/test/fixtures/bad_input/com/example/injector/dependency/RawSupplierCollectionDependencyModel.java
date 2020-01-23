package com.example.injector.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface RawSupplierCollectionDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  Collection<Supplier> getMyThing();
}
