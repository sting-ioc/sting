package com.example.injector.outputs;

import java.util.Collection;
import java.util.Optional;
import sting.Injectable;
import sting.Injector;

@Injector
public interface OptionalCollectionOutputModel
{
  Optional<Collection<MyModel>> getMyModel();

  @Injectable
  class MyModel
  {
  }
}
