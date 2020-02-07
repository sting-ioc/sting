package com.example.injectable.inputs;

import sting.Injectable;
import sting.NecessityType;
import sting.Service;

@Injectable
public class ExplicitOptionalPrimitiveInputModel
{
  ExplicitOptionalPrimitiveInputModel( @Service( necessity = NecessityType.OPTIONAL ) int someValue )
  {
  }
}
