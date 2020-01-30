package com.example.injector.inputs;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Injector;

@Injector
public interface NullableCollectionInputModel
{
  @Nullable
  Collection<String> getMyThing();
}
