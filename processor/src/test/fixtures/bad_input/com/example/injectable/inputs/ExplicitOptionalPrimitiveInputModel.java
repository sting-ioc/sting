package com.example.injectable.inputs;

import sting.Dependency;
import sting.Injectable;
import sting.NecessityType;

@Injectable
public class ExplicitOptionalPrimitiveInputModel
{
  ExplicitOptionalPrimitiveInputModel( @Dependency( necessity = NecessityType.OPTIONAL ) int someValue )
  {
  }
}
