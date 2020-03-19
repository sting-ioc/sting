package com.example.autofragment;

import sting.ContributeTo;
import sting.Fragment;

@ContributeTo( "BasicAutoFragmentModel" )
@Fragment
interface BasicAutoFragmentFragmentModel
{
  default String provideString()
  {
    return "";
  }
}
