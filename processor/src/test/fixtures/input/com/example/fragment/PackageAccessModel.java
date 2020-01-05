package com.example.fragment;

import sting.Fragment;

@Fragment
interface PackageAccessModel
{
  default Runnable provideX()
  {
    return null;
  }
}
