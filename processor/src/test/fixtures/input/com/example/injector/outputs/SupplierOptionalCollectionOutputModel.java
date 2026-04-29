package com.example.injector.outputs;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
interface SupplierOptionalCollectionOutputModel
{
  Collection<Supplier<Optional<MyModel>>> getMyModel();

  @Injectable
  class MyModel
  {
  }
}
