package com.example.injector;

import sting.Fragment;
import sting.Injector;

@Injector( includes = IncludeInjectorModel.InnerInjectorModel.class )
interface IncludeInjectorModel
{
  MyModel getMyModel();

  @Injector( includes = MyFragment.class )
  interface InnerInjectorModel
  {
    MyModel getMyModel();

  }

  @Fragment
  interface MyFragment
  {
    default MyModel provideRunnable()
    {
      return new MyModel();
    }
  }

  class MyModel
  {
  }
}
