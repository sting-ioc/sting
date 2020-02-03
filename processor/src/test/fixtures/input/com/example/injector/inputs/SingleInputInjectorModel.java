package com.example.injector.inputs;

import sting.Service;
import sting.Injector;

@Injector( inputs = @Service( type = Runnable.class ) )
interface SingleInputInjectorModel
{
  Runnable getRunnable();
}
