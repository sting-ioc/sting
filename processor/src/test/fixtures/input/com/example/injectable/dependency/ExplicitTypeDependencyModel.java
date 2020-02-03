package com.example.injectable.dependency;

import sting.Service;
import sting.Injectable;

@Injectable
public class ExplicitTypeDependencyModel
{
  ExplicitTypeDependencyModel( @Service( type = MyModel.class ) Runnable runnable )
  {
  }

  @Injectable
  public static class MyModel
    implements Runnable
  {
    @Override
    public void run()
    {
    }
  }
}
