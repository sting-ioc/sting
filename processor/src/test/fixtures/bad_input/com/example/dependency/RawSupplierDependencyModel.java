package com.example.dependency;

import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class RawSupplierDependencyModel
{
  @SuppressWarnings( "rawtypes" )
  RawSupplierDependencyModel( Supplier someValue )
  {
  }
}
