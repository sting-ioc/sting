package com.example.injector.dependency;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector
interface QualifiedDependencyModel
{
  @Dependency( qualifier = "Foo" )
  MyModel getMyModel();

  @Injectable( qualifier = "Foo" )
  class MyModel
  {
  }
}
