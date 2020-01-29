package com.example.deprecated;

import sting.Injectable;
import sting.Injector;

@Injector
@Deprecated
public interface DeprecatedInjectorModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
