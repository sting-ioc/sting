package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
public abstract class MultipleConstructorClassInjector
{
  MultipleConstructorClassInjector()
  {
  }

  MultipleConstructorClassInjector( int i )
  {
  }

  abstract MyModel getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
