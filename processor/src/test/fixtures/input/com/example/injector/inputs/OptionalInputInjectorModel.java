package com.example.injector.inputs;

import sting.Injector;

@Injector( inputs = @Injector.Input( type = Runnable.class, optional = true ) )
interface OptionalInputInjectorModel
{
  Runnable getRunnable();
}
