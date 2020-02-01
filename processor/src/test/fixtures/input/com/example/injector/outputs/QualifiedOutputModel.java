package com.example.injector.outputs;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector
interface QualifiedOutputModel
{
  @Dependency( qualifier = "Foo" )
  MyModel getMyModel();

  @Injectable( qualifier = "Foo" )
  class MyModel
  {
  }
}
