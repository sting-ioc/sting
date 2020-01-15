package com.example.injector.includes;

import sting.Dependency;
import sting.Factory;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import sting.Provides;

@Injector( includes = { RecursiveIncludesModel.MyFragment1.class,
                        RecursiveIncludesModel.MyFactory1.class,
                        RecursiveIncludesModel.MyModel1.class } )
abstract class RecursiveIncludesModel
{
  abstract Runnable getRunnable1();

  @Dependency( qualifier = "Fragment2" )
  abstract Runnable getRunnable2();

  @Dependency( qualifier = "Fragment3" )
  abstract Runnable getRunnable3();

  // TODO: Re-enable when factories implemented
  //abstract MyFactory1 getMyFactory1();
  //
  //abstract MyFactory2 getMyFactory2();
  //
  //abstract MyFactory3 getMyFactory3();

  abstract MyModel1 getMyModel1();

  abstract MyModel2 getMyModel2();

  abstract MyModel3 getMyModel3();

  @Fragment( includes = { MyFragment2.class, MyModel2.class, MyFactory2.class } )
  public interface MyFragment1
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }

  @Factory
  public interface MyFactory1
  {
    Runnable create();
  }

  @Injectable
  static class MyModel1
  {
  }

  @Fragment( includes = { MyFragment3.class, MyFactory3.class, MyModel3.class } )
  public interface MyFragment2
  {
    @Provides( qualifier = "Fragment2" )
    default Runnable provideRunnable()
    {
      return null;
    }
  }

  @Factory
  public interface MyFactory2
  {
    Runnable create();
  }

  @Injectable
  static class MyModel2
  {
  }

  @Fragment
  public interface MyFragment3
  {
    @Provides( qualifier = "Fragment3" )
    default Runnable provideRunnable()
    {
      return null;
    }
  }

  @Factory
  public interface MyFactory3
  {
    Runnable create();
  }

  @Injectable
  static class MyModel3
  {
  }
}
