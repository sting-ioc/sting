package com.example.injectable.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class WildcardSupplierCollectionDependencyModel
{
  WildcardSupplierCollectionDependencyModel( Collection<Supplier<?>> someValue )
  {
  }
}
