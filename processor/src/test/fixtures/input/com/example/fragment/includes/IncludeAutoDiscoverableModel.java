package com.example.fragment.includes;

import sting.Fragment;
import sting.Injectable;

public interface IncludeAutoDiscoverableModel
{
  @Fragment( includes = MyAutoDiscoverableModel.class )
  public interface MyFragment
  {
  }

  @Injectable
  class MyAutoDiscoverableModel
  {
  }
}
