package com.example.injector.outputs;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Dependency;
import sting.Injector;
import sting.NecessityType;

@Injector
public interface ExplicitOptionalCollectionOutputModel
{
  @Dependency( necessity = NecessityType.OPTIONAL )
  @Nullable
  Collection<String> getMyThing();
}
