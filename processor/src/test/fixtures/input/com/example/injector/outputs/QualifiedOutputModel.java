package com.example.injector.outputs;

import sting.Service;
import sting.Injectable;
import sting.Injector;

@Injector
interface QualifiedOutputModel
{
  @Service( qualifier = "Foo" )
  MyModel getMyModel();

  @Injectable( qualifier = "Foo" )
  class MyModel
  {
  }
}
