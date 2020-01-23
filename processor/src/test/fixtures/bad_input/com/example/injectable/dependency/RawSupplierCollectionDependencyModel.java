package com.example.injectable.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class RawSupplierCollectionDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  RawSupplierCollectionDependencyModel( Collection<Supplier> someValue )
  {
  }
}
