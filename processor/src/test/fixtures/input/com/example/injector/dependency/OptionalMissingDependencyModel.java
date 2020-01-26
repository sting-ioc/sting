package com.example.injector.dependency;

import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
interface OptionalMissingDependencyModel
{
  @Nullable
  MyModel1 getMyModel1();

  MyModel2 getMyModel2();

  class MyModel1
  {
  }

  @Injectable
  class MyModel2
  {
  }
}
