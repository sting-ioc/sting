package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
public abstract class SuppressedPublicConstructorInjector
{
  @SuppressWarnings( "Sting:PublicConstructor" )
  public SuppressedPublicConstructorInjector()
  {
  }

  abstract MyModel getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
