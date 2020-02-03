package com.example.injectable.inputs;

import sting.Service;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalPrimitiveInputModel
{
  ExplicitOptionalPrimitiveInputModel( @Service( necessity = NecessityType.OPTIONAL ) int someValue )
  {
  }
}
