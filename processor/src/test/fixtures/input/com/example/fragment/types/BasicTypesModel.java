package com.example.fragment.types;

import java.util.EventListener;
import java.util.Properties;
import sting.Fragment;
import sting.Typed;

@Fragment
public interface BasicTypesModel
{
  // Does not expose BasicTypesModel or AutoCloseable nor Hashtable and related
  @Typed( { Runnable.class, EventListener.class, Object.class, Properties.class } )
  default MyModel provideMyModel()
  {
    return null;
  }
}
