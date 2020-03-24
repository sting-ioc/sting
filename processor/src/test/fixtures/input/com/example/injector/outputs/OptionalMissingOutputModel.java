package com.example.injector.outputs;

import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
public interface OptionalMissingOutputModel
{
  @Nullable
  Runnable getRunnable();

  MyModel2 getMyModel2();

  @Injectable
  class MyModel2
  {
  }
}
