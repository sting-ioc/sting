package com.example.fragment.includes;

import sting.Fragment;

@Fragment
public interface Included2Model
{
  default Runnable provideRunnable()
  {
    return null;
  }
}
