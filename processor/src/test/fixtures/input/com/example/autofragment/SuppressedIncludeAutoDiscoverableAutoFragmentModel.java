package com.example.autofragment;

import sting.AutoFragment;
import sting.ContributeTo;
import sting.Injectable;

@SuppressWarnings( "Sting:AutoDiscoverableContributed" )
public interface SuppressedIncludeAutoDiscoverableAutoFragmentModel
{
  @AutoFragment( "com.example.autofragment.include_autodiscoverable.MyAutoFragmentModel" )
  public interface MyAutoFragment
  {
  }

  @ContributeTo( "com.example.autofragment.include_autodiscoverable.MyAutoFragmentModel" )
  @Injectable
  class MyInjectableModel
  {
  }
}
