package com.example.injector.dependency;

import java.util.function.Supplier;
import sting.Injector;

@Injector
public interface MethodReturningVoidDependencyModel
{
  void getMyThing();
}
