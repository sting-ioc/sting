package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
public abstract class PublicConstructorInjector
{
  public PublicConstructorInjector()
  {
  }

  abstract MyModel getMyModel();

  @Injectable
  static class MyModel
  {
  }
}
