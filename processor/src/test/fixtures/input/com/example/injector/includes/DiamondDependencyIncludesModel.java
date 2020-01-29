package com.example.injector.includes;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { DiamondDependencyIncludesModel.MyModel.class,
                        DiamondDependencyIncludesModel.MyFragment1.class,
                        DiamondDependencyIncludesModel.MyFragment2.class } )
interface DiamondDependencyIncludesModel
{
  Runnable getRunnable();

  @Fragment( includes = MyFragment3.class )
  interface MyFragment1
  {
  }

  @Fragment( includes = MyFragment3.class )
  interface MyFragment2
  {
  }

  @Fragment( includes = MyModel.class )
  interface MyFragment3
  {
  }

  @Injectable( eager = true )
  class MyModel
  {
  }
}
