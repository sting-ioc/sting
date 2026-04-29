package com.example.injectable.dependency;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class SupplierOptionalCollectionDependencyModel
{
  SupplierOptionalCollectionDependencyModel( Collection<Supplier<Optional<Runnable>>> runnable )
  {
  }
}
