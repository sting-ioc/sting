package com.example.injector.includes;

import sting.Dependency;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import sting.Provides;

@Injector( includes = { RecursiveIncludesModel.MyFragment1.class,
                        RecursiveIncludesModel.MyModel1.class } )
interface RecursiveIncludesModel
{
  Runnable getRunnable1();

  @Dependency( qualifier = "Fragment2" )
  Runnable getRunnable2();

  @Dependency( qualifier = "Fragment3" )
  Runnable getRunnable3();

  MyModel1 getMyModel1();

  MyModel2 getMyModel2();

  MyModel3 getMyModel3();

  @Fragment( includes = { MyFragment2.class, MyModel2.class } )
  interface MyFragment1
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }

  @Injectable
  class MyModel1
  {
  }

  @Fragment( includes = { MyFragment3.class, MyModel3.class } )
  interface MyFragment2
  {
    @Provides( qualifier = "Fragment2" )
    default Runnable provideRunnable()
    {
      return null;
    }
  }

  @Injectable
  class MyModel2
  {
  }

  @Fragment
  interface MyFragment3
  {
    @Provides( qualifier = "Fragment3" )
    default Runnable provideRunnable()
    {
      return null;
    }
  }

  @Injectable
  class MyModel3
  {
  }
}
