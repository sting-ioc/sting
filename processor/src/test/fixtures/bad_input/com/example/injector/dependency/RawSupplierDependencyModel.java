package com.example.injector.dependency;

import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface RawSupplierDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  Supplier getMyThing();
}
