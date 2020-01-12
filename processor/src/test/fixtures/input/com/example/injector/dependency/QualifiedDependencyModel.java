package com.example.injector.dependency;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector
abstract class QualifiedDependencyModel
{
  @Dependency( qualifier = "Foo" )
  abstract MyModel getMyModel();

  @Injectable( qualifier = "Foo" )
  static class MyModel
  {
  }
}
