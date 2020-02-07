package com.example.injector.includes.recursive;

import sting.Fragment;
import sting.Named;

@Fragment( includes = { MyFragment3.class, MyModel3.class } )
interface MyFragment2
{
  @Named( "Fragment2" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
