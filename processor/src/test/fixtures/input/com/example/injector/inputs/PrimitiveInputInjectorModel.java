package com.example.injector.inputs;

import sting.Injector;

@Injector( inputs = {
  @Injector.Input( type = boolean.class ),
  @Injector.Input( type = char.class ),
  @Injector.Input( type = byte.class ),
  @Injector.Input( type = short.class ),
  @Injector.Input( type = int.class ),
  @Injector.Input( type = long.class ),
  @Injector.Input( type = float.class ),
  @Injector.Input( type = double.class )
} )
interface PrimitiveInputInjectorModel
{
  boolean value1();
}
