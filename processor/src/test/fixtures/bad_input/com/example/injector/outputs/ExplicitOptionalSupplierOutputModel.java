package com.example.injector.outputs;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Injector;
import sting.NecessityType;
import sting.Service;

@Injector
public interface ExplicitOptionalSupplierOutputModel
{
  @Service( necessity = NecessityType.OPTIONAL )
  @Nullable
  Supplier<String> getMyThing();
}
