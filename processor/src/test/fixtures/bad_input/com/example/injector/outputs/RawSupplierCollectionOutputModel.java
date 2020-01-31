package com.example.injector.outputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface RawSupplierCollectionOutputModel
{
  @SuppressWarnings( "rawtypes" )
  Collection<Supplier> getMyThing();
}
