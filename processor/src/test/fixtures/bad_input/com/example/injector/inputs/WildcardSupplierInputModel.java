package com.example.injector.inputs;

import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface WildcardSupplierInputModel
{
  Supplier<?> getMyThing();
}
