package com.example.fragment.named;

import javax.inject.Named;
import sting.Fragment;

@Fragment
public interface Jsr330NamedProvidesModel
{
  @Named
  default Runnable provideRunnable()
  {
    return null;
  }
}
