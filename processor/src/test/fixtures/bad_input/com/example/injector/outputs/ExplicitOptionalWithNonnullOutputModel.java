package com.example.injector.outputs;

import javax.annotation.Nonnull;
import sting.Injector;
import sting.NecessityType;
import sting.Service;

@Injector
public interface ExplicitOptionalWithNonnullOutputModel
{
  @Service( necessity = NecessityType.OPTIONAL )
  @Nonnull
  String getMyThing();
}
