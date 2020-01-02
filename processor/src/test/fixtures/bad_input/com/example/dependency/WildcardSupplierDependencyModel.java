package com.example.dependency;

import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class WildcardSupplierDependencyModel
{
  WildcardSupplierDependencyModel( Supplier<?> someValue )
  {
  }
}
