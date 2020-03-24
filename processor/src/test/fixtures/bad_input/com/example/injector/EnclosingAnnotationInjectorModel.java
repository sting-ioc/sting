package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
interface EnclosingAnnotationInjectorModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }

  @interface Foo
  {
  }
}
