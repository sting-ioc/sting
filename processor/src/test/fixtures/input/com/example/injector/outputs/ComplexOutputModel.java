package com.example.injector.outputs;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Injectable;
import sting.Injector;
import sting.Named;

@Injector
interface ComplexOutputModel
{
  MyModel1 getMyModel1();

  Supplier<MyModel2> getMyModel2Supplier();

  @Named( "foo" )
  Supplier<MyModel3> getMyModel3Supplier();

  @Nullable
  Runnable getRunnable();

  @Injectable
  class MyModel1
  {
  }

  @Injectable
  class MyModel2
  {
  }

  @Injectable
  @Named( "foo" )
  class MyModel3
  {
  }
}
