package com.example.injector.dependency;

import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
interface OptionalDependencyModel
{
  @Nullable
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
