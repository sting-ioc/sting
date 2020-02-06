package com.example.injector.inputs;

import sting.Named;
import sting.Service;
import sting.Injectable;
import sting.Injector;

@Injector( inputs = { @Service( type = Runnable.class ),
                      @Service( qualifier = "hostname", type = String.class ) } )
interface MultipleInputInjectorModel
{
  MyModel getMyModel();

  @Named( "hostname" )
  String getHostname();

  @Injectable
  class MyModel
  {
    MyModel( Runnable runnable )
    {
    }
  }
}
