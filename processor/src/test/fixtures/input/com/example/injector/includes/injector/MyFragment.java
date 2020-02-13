package com.example.injector.includes.injector;

import sting.Fragment;

@Fragment
public interface MyFragment
{
  default MyModel provideRunnable()
  {
    return new MyModel();
  }
}
