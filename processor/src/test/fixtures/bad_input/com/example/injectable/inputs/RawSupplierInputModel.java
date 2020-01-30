package com.example.injectable.inputs;

import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class RawSupplierInputModel
{
  @SuppressWarnings( "rawtypes" )
  RawSupplierInputModel( Supplier someValue )
  {
  }
}
