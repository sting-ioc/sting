package com.example.injector.dependency;

import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
abstract class OptionalDependencyModel
{
  @Nullable
  abstract MyModel getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
