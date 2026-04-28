package com.example.injector.includes;

import sting.Injectable;
import sting.Injector;

@Injector( includes = FragmentOnlyIncludesModel.MyModel.class )
interface FragmentOnlyIncludesModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
