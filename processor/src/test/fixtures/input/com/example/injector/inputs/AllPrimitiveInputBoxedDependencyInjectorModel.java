package com.example.injector.inputs;

import sting.Injectable;
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
interface AllPrimitiveInputBoxedDependencyInjectorModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
    MyModel( final Boolean value1,
             final Character value2,
             final Byte value3,
             final Short value4,
             final Integer value5,
             final Long value6,
             final Float value7,
             final Double value8 )
    {
    }
  }
}
