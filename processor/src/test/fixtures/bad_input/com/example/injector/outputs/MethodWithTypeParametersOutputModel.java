package com.example.injector.outputs;

import sting.Injector;

@Injector
public interface MethodWithTypeParametersOutputModel
{
  <T> String getMyThing();
}
