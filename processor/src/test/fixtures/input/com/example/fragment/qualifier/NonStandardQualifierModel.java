package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Named;

@Fragment
public interface NonStandardQualifierModel
{
  @Named( "\u200E\uD83C\uDF89 Tada!" )
  default Runnable provideRunnable()
  {
    return null;
  }
}
