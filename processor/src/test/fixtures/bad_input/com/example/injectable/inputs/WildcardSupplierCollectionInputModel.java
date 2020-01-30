package com.example.injectable.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class WildcardSupplierCollectionInputModel
{
  WildcardSupplierCollectionInputModel( Collection<Supplier<?>> someValue )
  {
  }
}
