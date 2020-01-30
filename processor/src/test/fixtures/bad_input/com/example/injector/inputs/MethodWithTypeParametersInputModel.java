package com.example.injector.inputs;

import sting.Injector;

@Injector
public interface MethodWithTypeParametersInputModel
{
  <T> String getMyThing();
}
