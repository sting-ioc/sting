package com.example.injector.outputs;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Service;
import sting.Injector;
import sting.NecessityType;

@Injector
public interface ExplicitOptionalSupplierOutputModel
{
  @Service( necessity = NecessityType.OPTIONAL )
  @Nullable
  Supplier<String> getMyThing();
}
