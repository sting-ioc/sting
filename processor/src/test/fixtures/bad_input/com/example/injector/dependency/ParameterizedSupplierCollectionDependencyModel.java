package com.example.injector.dependency;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface ParameterizedSupplierCollectionDependencyModel
{
  Collection<Supplier<List<String>>> getMyThing();
}
