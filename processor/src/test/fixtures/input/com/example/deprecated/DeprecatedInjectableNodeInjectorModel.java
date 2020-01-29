package com.example.deprecated;

import sting.Injectable;
import sting.Injector;

@Injector
public interface DeprecatedInjectableNodeInjectorModel
{
  MyModel getMyModel();

  @Deprecated
  @Injectable
  class MyModel
  {
  }
}
