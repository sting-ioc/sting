package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
interface TypeParametersInjectorModel<T extends Runnable>
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
