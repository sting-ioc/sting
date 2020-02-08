package com.example.injector.inputs;

import sting.Injector;

@Injector( inputs = @Injector.Service( type = Runnable.class, optional = true ) )
interface OptionalInputInjectorModel
{
  Runnable getRunnable();
}
