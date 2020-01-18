package com.example.injector.dependency;

import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
abstract class OptionalMissingDependencyModel
{
  @Nullable
  abstract MyModel1 getMyModel1();

  abstract MyModel2 getMyModel2();

  static class MyModel1
  {
  }

  @Injectable
  static class MyModel2
  {
  }
}
