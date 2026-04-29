package com.example.injectable.dependency;

import java.util.Optional;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
public class SupplierOptionalDependencyModel
{
  SupplierOptionalDependencyModel( Supplier<Optional<Runnable>> runnable )
  {
  }
}
