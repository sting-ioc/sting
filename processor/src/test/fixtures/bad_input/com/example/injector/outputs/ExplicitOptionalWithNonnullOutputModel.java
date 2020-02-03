package com.example.injector.outputs;

import javax.annotation.Nonnull;
import sting.Service;
import sting.Injector;
import sting.NecessityType;

@Injector
public interface ExplicitOptionalWithNonnullOutputModel
{
  @Service( necessity = NecessityType.OPTIONAL )
  @Nonnull
  String getMyThing();
}
