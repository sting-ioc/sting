package com.example.injector.outputs;

import javax.annotation.Nonnull;
import sting.Dependency;
import sting.Injector;
import sting.NecessityType;

@Injector
public interface ExplicitOptionalWithNonnullOutputModel
{
  @Dependency( necessity = NecessityType.OPTIONAL )
  @Nonnull
  String getMyThing();
}
