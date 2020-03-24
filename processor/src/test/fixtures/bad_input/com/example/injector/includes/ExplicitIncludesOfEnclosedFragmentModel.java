package com.example.injector.includes;

import sting.Fragment;
import sting.Injector;

@Injector( includes = ExplicitIncludesOfEnclosedFragmentModel.MyFragment.class )
interface ExplicitIncludesOfEnclosedFragmentModel
{
  Runnable getRunnable();

  @Fragment
  interface MyFragment
  {
    default Runnable provideRunnable()
    {
      return null;
    }
  }
}
