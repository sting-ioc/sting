package com.example.injector.includes;

import sting.Injectable;
import sting.Injector;

@Injector( includes = byte.class )
public interface PrimitiveInIncludesModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
