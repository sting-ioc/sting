package com.example.injector.inputs;

import sting.Injectable;
import sting.Injector;
import sting.Named;

@Injector( inputs = { @Injector.Input( type = Runnable.class ),
                      @Injector.Input( qualifier = "hostname", type = String.class ) } )
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
