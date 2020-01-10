package com.example.injector.includes;

import java.util.EventListener;
import sting.Injectable;
import sting.Injector;

@Injector( includes = EventListener.class )
public interface BadTypesInIncludesModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
