package com.example.injector.inputs;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface ParameterizedSupplierCollectionInputModel
{
  Collection<Supplier<List<String>>> getMyThing();
}
