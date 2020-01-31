package com.example.injector.outputs;

import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface WildcardSupplierOutputModel
{
  Supplier<?> getMyThing();
}
