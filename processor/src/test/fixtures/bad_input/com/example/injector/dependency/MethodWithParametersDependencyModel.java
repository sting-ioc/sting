package com.example.injector.dependency;

import sting.Injector;

@Injector
public interface MethodWithParametersDependencyModel
{
  String getMyThing( int i );
}
