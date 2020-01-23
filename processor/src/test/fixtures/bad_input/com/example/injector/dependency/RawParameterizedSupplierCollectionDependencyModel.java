package com.example.injector.dependency;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface RawParameterizedSupplierCollectionDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  Collection<Supplier<List>> getMyThing();
}
