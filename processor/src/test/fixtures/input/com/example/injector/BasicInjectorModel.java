package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
interface BasicInjectorModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
