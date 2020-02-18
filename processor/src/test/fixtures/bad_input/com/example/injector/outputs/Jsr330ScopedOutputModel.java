package com.example.injector.outputs;

import javax.inject.Singleton;
import sting.Injector;

@Injector
public interface Jsr330ScopedOutputModel
{
  @Singleton
  String getMyThing();
}
