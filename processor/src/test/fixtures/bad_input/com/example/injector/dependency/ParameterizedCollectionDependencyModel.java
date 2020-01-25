package com.example.injector.dependency;

import java.util.Collection;
import java.util.List;
import sting.Injector;

@Injector
public interface ParameterizedCollectionDependencyModel
{
  Collection<List<String>> getMyThing();
}
