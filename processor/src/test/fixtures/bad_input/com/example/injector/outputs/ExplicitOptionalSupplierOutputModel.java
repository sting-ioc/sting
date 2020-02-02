package com.example.injector.outputs;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Dependency;
import sting.Injector;
import sting.NecessityType;

@Injector
public interface ExplicitOptionalSupplierOutputModel
{
  @Dependency( necessity = NecessityType.OPTIONAL )
  @Nullable
  Supplier<String> getMyThing();
}
