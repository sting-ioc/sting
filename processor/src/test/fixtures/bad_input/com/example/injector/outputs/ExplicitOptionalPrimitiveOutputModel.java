package com.example.injector.outputs;

import sting.Injector;
import sting.NecessityType;
import sting.Service;

@Injector
public interface ExplicitOptionalPrimitiveOutputModel
{
  @Service( necessity = NecessityType.OPTIONAL )
  int getMyThing();
}
