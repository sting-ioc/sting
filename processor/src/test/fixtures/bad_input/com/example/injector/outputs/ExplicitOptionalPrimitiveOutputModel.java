package com.example.injector.outputs;

import sting.Service;
import sting.Injector;
import sting.NecessityType;

@Injector
public interface ExplicitOptionalPrimitiveOutputModel
{
  @Service( necessity = NecessityType.OPTIONAL )
  int getMyThing();
}
