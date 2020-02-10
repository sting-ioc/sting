package com.example.fragment.named;

import javax.inject.Named;
import sting.Fragment;

@Fragment
public interface Jsr330NamedInputModel
{
  default Runnable provideRunnable( @Named int priority )
  {
    return null;
  }
}
