package com.example.injector.outputs;

import java.util.Collection;
import java.util.List;
import sting.Injector;

@Injector
public interface ParameterizedCollectionOutputModel
{
  Collection<List<String>> getMyThing();
}
