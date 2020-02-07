package com.example.injector.inputs;

import sting.Injectable;
import sting.Injector;
import sting.Named;

@Injector( inputs = { @Injector.Service( type = Runnable.class ),
                      @Injector.Service( qualifier = "hostname", type = String.class ) } )
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
