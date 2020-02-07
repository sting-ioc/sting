package com.example.injectable.inputs;

import java.util.function.Supplier;
import sting.Injectable;
import sting.NecessityType;
import sting.Service;

@Injectable
public class ExplicitOptionalSupplierInputModel
{
  ExplicitOptionalSupplierInputModel( @Service( necessity = NecessityType.OPTIONAL ) Supplier<String> someValue )
  {
  }
}
