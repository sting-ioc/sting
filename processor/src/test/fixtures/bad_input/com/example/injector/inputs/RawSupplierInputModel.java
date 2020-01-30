package com.example.injector.inputs;

import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface RawSupplierInputModel
{
  @SuppressWarnings( "rawtypes" )
  Supplier getMyThing();
}
