package com.example.injector.outputs;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface RawParameterizedSupplierCollectionOutputModel
{
  @SuppressWarnings( "rawtypes" )
  Collection<Supplier<List>> getMyThing();
}
