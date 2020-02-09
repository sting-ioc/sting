package com.example.injector.inputs;

import javax.annotation.Nullable;
import sting.Injector;

@Injector( inputs = @Injector.Input( type = Runnable.class, optional = true ) )
interface OptionalInputInjectorModel
{
  @Nullable
  Runnable getRunnable();
}
