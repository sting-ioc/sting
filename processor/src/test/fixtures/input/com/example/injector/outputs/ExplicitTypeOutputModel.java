package com.example.injector.outputs;

import sting.Service;
import sting.Injectable;
import sting.Injector;

@Injector
interface ExplicitTypeOutputModel
{
  @Service( type = MyModel.class )
  Runnable getMyModel();

  @Injectable
  class MyModel
    implements Runnable
  {
    @Override
    public void run()
    {
    }
  }
}
