package com.example.injector.inputs;

import sting.Injectable;
import sting.Injector;

@Injector( inputs = @Injector.Input( type = Runnable.class, optional = true ) )
interface MismatchOptionalityInputInjectorModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
    MyModel( Runnable runnable )
    {
    }
  }
}
