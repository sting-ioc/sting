package com.example.injector;

import javax.inject.Singleton;
import sting.Injector;

@SuppressWarnings( "CdiManagedBeanInconsistencyInspection" )
@Singleton
@Injector
public interface Jsr330ScopedInjectorModel
{
  String getMyThing();
}
