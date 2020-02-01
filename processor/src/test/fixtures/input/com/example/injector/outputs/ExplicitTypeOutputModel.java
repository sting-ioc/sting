package com.example.injector.outputs;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector
interface ExplicitTypeOutputModel
{
  @Dependency( type = MyModel.class )
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
