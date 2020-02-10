package com.example.injector.named;

import javax.inject.Named;
import sting.Injectable;
import sting.Injector;

@Injector
interface Jsr330NamedOutputModel
{
  @Named( "X" )
  MyModel getMyModel();

  @Injectable
  class MyModel
  {
  }
}
