package com.example.autofragment;

import sting.AutoFragment;
import sting.ContributeTo;
import sting.Injectable;

public interface IncludeAutoDiscoverableAutoFragmentModel
{
  @AutoFragment( "com.example.autofragment.include_autodiscoverable.MyAutoFragmentModel" )
  interface MyAutoFragment
  {
  }

  @ContributeTo( "com.example.autofragment.include_autodiscoverable.MyAutoFragmentModel" )
  @Injectable
  class MyInjectableModel
  {
  }
}
