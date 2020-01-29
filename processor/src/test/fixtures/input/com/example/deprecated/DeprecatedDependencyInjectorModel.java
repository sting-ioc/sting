package com.example.deprecated;

import sting.Injectable;
import sting.Injector;

@Injector
public interface DeprecatedDependencyInjectorModel
{
  @Deprecated
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
