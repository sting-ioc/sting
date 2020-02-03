package com.example.injectable.inputs;

import java.util.function.Supplier;
import sting.Service;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalSupplierInputModel
{
  ExplicitOptionalSupplierInputModel( @Service( necessity = NecessityType.OPTIONAL ) Supplier<String> someValue )
  {
  }
}