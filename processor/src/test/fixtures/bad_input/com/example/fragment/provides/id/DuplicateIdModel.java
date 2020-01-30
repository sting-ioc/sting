package com.example.fragment.provides.id;

import sting.Fragment;
import sting.Provides;

@Fragment
public interface DuplicateIdModel
{
  @Provides( id = "RunLolaRun" )
  default Runnable runnable2()
  {
    return null;
  }

  @Provides( id = "RunLolaRun" )
  default Runnable runnable1()
  {
    return null;
  }
}
