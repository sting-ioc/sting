package com.example.integration.todomvc.model;

import sting.Eager;
import sting.Fragment;

@Fragment
public interface BrowserLocationFragment
{
  @Eager
  default BrowserLocation createBrowserLocation()
  {
    return new Arez_BrowserLocation();
  }
}
