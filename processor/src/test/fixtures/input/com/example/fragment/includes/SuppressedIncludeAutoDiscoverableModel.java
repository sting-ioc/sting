package com.example.fragment.includes;

import sting.Fragment;
import sting.Injectable;

@SuppressWarnings( "Sting:AutoDiscoverableIncluded" )
public interface SuppressedIncludeAutoDiscoverableModel
{
  @Fragment( includes = MyAutoDiscoverableModel.class )
  interface MyFragment
  {
  }

  @Injectable
  class MyAutoDiscoverableModel
  {
  }
}
