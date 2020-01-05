package com.example.fragment.includes;

import sting.Fragment;

@Fragment
public interface Included3Model
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
