package com.example.injector.dependency;

import sting.Injector;

@Injector
public interface MethodWithTypeParametersDependencyModel
{
  <T> String getMyThing();
}
