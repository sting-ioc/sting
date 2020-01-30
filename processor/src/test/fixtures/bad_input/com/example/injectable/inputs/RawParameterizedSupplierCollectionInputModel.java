package com.example.injectable.inputs;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class RawParameterizedSupplierCollectionInputModel
{
  @SuppressWarnings( "rawtypes" )
  RawParameterizedSupplierCollectionInputModel( Collection<Supplier<List>> someValue )
  {
  }
}
