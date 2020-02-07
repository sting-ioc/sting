package com.example.injector.outputs;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Injector;
import sting.NecessityType;
import sting.Service;

@Injector
public interface ExplicitOptionalCollectionOutputModel
{
  @Service( necessity = NecessityType.OPTIONAL )
  @Nullable
  Collection<String> getMyThing();
}
