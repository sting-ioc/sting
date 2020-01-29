package com.example.injectable.dependency;

import sting.Dependency;
import sting.Injectable;

@Injectable
public class ExplicitTypeDependencyModel
{
  ExplicitTypeDependencyModel( @Dependency( type = MyModel.class ) Runnable runnable )
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
