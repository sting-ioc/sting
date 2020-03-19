package com.example.multiround.autofragment;

import sting.ContributeTo;
import sting.Fragment;

@ContributeTo( "MyAutoFragment" )
@Fragment
interface MyFragment
{
  default String provideString()
  {
    return "";
  }
}
