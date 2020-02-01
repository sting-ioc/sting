package com.example.injector.outputs;

import sting.Injectable;
import sting.Injector;

@Injector
interface BasicOutputModel
{
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
