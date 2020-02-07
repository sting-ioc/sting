package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Named;

@Fragment
public interface BasicQualifierModel
{
  @Named( "com.bix/SomeQualifier" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
