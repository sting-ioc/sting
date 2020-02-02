package com.example.injectable.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Dependency;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalSupplierInputModel
{
  ExplicitOptionalSupplierInputModel( @Dependency( necessity = NecessityType.OPTIONAL ) Supplier<String> someValue )
  {
  }
}
