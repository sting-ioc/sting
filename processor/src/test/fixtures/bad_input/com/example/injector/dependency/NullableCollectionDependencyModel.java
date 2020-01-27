package com.example.injector.dependency;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Injector;

@Injector
public interface NullableCollectionDependencyModel
{
  @Nullable
  Collection<String> getMyThing();
}
