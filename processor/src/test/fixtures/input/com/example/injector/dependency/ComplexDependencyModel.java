package com.example.injector.dependency;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector
abstract class ComplexDependencyModel
{
  abstract MyModel1 getMyModel1();

  @Nullable
  abstract Supplier<MyModel2> getMyModel2Supplier();

  @Dependency( qualifier = "foo" )
  abstract Supplier<MyModel3> getMyModel3Supplier();

  @Nullable
  abstract MyModel4 getMyModel4();

  @Injectable
  static class MyModel1
  {
  }

  @Injectable
  static class MyModel2
  {
  }

  @Injectable( qualifier = "foo" )
  static class MyModel3
  {
  }

  // Not @Injectable and thus would need to be provided if used
  // but not used and nullable Dependency
  static class MyModel4
  {
  }
}
