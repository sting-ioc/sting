package com.example.injector.inputs;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector( inputs = { @Dependency( type = Runnable.class ),
                      @Dependency( qualifier = "hostname", type = String.class ) } )
interface MultipleInputInjectorModel
{
  MyModel getMyModel();

  @Dependency( qualifier = "hostname" )
  String getHostname();

  @Injectable
  class MyModel
  {
    MyModel( Runnable runnable )
    {
    }
  }
}
