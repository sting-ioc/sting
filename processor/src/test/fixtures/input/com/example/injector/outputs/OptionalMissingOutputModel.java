package com.example.injector.outputs;

import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
public interface OptionalMissingOutputModel
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
