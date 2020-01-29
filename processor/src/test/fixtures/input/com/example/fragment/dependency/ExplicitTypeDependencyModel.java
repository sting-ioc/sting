package com.example.fragment.dependency;

import sting.Dependency;
import sting.Fragment;
import sting.Injectable;

@Fragment
public interface ExplicitTypeDependencyModel
{
  default Runnable provideRunnable( @Dependency( type = MyModel.class ) Runnable runnable )
  {
    return null;
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
