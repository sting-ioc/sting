package com.example.injector.outputs;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Injector;

@Injector
public interface NullableCollectionOutputModel
{
  @Nullable
  Collection<String> getMyThing();
}
