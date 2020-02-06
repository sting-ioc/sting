package com.example.injector.outputs;

import sting.Injectable;
import sting.Injector;
import sting.Named;
import sting.Service;

@Injector
interface QualifiedOutputModel
{
  @Named( "Foo" )
  MyModel getMyModel();

  @Injectable( services = @Service( qualifier = "Foo" ) )
  class MyModel
  {
  }
}
