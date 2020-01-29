package com.example.injector.dependency;

import sting.Dependency;
import sting.Injectable;
import sting.Injector;

@Injector
interface ExplicitTypeDependencyModel
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
