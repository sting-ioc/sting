package com.example.injector.dependency;

import javax.annotation.Nullable;
import sting.Injector;

@Injector
abstract class OptionalMissingDependencyModel
{
  @Nullable
  abstract MyModel getMyModel();

  static class MyModel
  {
  }
}
