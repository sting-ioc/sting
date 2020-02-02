package com.example.injector.outputs;

import sting.Dependency;
import sting.Injector;
import sting.NecessityType;

@Injector
public interface ExplicitOptionalPrimitiveOutputModel
{
  @Dependency( necessity = NecessityType.OPTIONAL )
  int getMyThing();
}
