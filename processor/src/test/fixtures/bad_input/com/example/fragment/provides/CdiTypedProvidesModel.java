package com.example.fragment.provides;

import javax.enterprise.inject.Typed;
import sting.Fragment;

@Fragment
public interface CdiTypedProvidesModel
{
  @SuppressWarnings( "CdiTypedAnnotationInspection" )
  @Typed( { Runnable.class, Object.class } )
  default Runnable provideRunnable()
  {
    return null;
  }
}
