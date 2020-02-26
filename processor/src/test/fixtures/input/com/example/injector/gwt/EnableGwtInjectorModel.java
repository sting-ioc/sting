package com.example.injector.gwt;

import sting.Feature;
import sting.Injectable;
import sting.Injector;

@Injector( gwt = Feature.ENABLE )
interface EnableGwtInjectorModel
{
  MyModel0 getMyModel();

  @Injectable
  class MyModel0
  {
  }
}
