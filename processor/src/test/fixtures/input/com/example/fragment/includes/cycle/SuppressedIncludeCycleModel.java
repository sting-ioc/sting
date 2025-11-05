package com.example.fragment.includes.cycle;

import sting.Fragment;

public interface SuppressedIncludeCycleModel
{
  @SuppressWarnings( "Sting:FragmentIncludeCycle" )
  @Fragment( includes = B.class )
  interface A
  {
  }

  @SuppressWarnings( "Sting:FragmentIncludeCycle" )
  @Fragment( includes = A.class )
  interface B
  {
  }
}
