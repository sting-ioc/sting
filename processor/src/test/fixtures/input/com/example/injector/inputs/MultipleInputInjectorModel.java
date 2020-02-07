package com.example.injector.inputs;

import sting.Injectable;
import sting.Injector;
import sting.Named;
import sting.Service;

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
